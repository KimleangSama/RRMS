package com.kkimleang.rrms.service.payment;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.enums.room.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.request.mapper.*;
import com.kkimleang.rrms.payload.request.payment.*;
import com.kkimleang.rrms.payload.response.payment.*;
import com.kkimleang.rrms.repository.payment.*;
import com.kkimleang.rrms.service.user.*;
import com.kkimleang.rrms.util.*;
import jakarta.transaction.*;
import java.math.*;
import java.time.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponse createPayment(CustomUserDetails userDetails, CreatePaymentRequest request) {
        User user = validateUser(userDetails);
        Invoice invoice = findAndValidateInvoice(request.getInvoiceId());
        Payment payment = createAndSavePayment(user, invoice, request);
        updateInvoiceStatus(invoice, payment);
        return PaymentResponse.fromPayment(user, payment);
    }

    @Transactional
    public List<PaymentResponse> getPaymentsOfInvoiceId(CustomUserDetails userDetails, UUID invoiceId) {
        User user = validateUser(userDetails);
        Invoice invoice = findAndValidateInvoice(invoiceId);
        validateUserAccess(user, invoice);
        return PaymentResponse.fromPayments(
                user,
                paymentRepository.findAllByInvoice(invoice)
        );
    }

    @Transactional
    public PaymentResponse editPayment(CustomUserDetails userDetails, UUID paymentId, EditPaymentInfoRequest request) {
        User user = validateUser(userDetails);
        Payment payment = findAndValidatePayment(paymentId);
        validateUserAccess(user, payment.getInvoice());
        PaymentMapper.mapToPayment(user, payment, request);
        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Payment updated: {}", updatedPayment);
        return PaymentResponse.fromPayment(user, updatedPayment);
    }

    private User validateUser(CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        DeletableEntityValidator.validate(user, "User");
        return user;
    }

    private Invoice findAndValidateInvoice(UUID invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .map(invoice -> {
                    validateInvoiceEntities(invoice);
                    return invoice;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));
    }

    private void validateInvoiceEntities(Invoice invoice) {
        DeletableEntityValidator.validate(invoice, "Invoice");
        DeletableEntityValidator.validate(invoice.getRoomAssignment(), "Room Assignment");
        DeletableEntityValidator.validate(invoice.getRoomAssignment().getRoom(), "Room");
        DeletableEntityValidator.validate(invoice.getRoomAssignment().getRoom().getProperty(), "Property");
    }

    private Payment findAndValidatePayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
        DeletableEntityValidator.validate(payment, "Payment");
        return payment;
    }

    private void validateUserAccess(User user, Invoice invoice) {
        boolean hasAccess = !PrivilegeChecker.withoutRight(user, invoice.getRoomAssignment().getRoom().getCreatedBy()) ||
                !PrivilegeChecker.withoutRight(user, invoice.getRoomAssignment().getRoom().getProperty().getCreatedBy());
        if (!hasAccess) {
            throw new ResourceForbiddenException("You are not allowed to access this resource", invoice.getId());
        }
    }

    private Payment createAndSavePayment(User user, Invoice invoice, CreatePaymentRequest request) {
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        PaymentMapper.mapToPayment(user, payment, request);
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created: {}", savedPayment);
        return savedPayment;
    }

    private void updateInvoiceStatus(Invoice invoice, Payment payment) {
        BigDecimal newAmountPaid = computeNewAmountPaid(invoice, payment);
        BigDecimal totalAmount = BigDecimal.valueOf(invoice.getTotalAmount());
        invoice.setUpdatedAt(Instant.now());
        invoice.setUpdatedBy(payment.getCreatedBy());
        invoice.setAmountPaid(newAmountPaid.doubleValue());
        invoice.setAmountDue(totalAmount.subtract(newAmountPaid).doubleValue());
        invoice.setInvoiceStatus(calculateInvoiceStatus(newAmountPaid, totalAmount));
        invoiceRepository.save(invoice);
    }

    private BigDecimal computeNewAmountPaid(Invoice invoice, Payment payment) {
        return BigDecimal.valueOf(invoice.getAmountPaid())
                .add(BigDecimal.valueOf(payment.getAmountPaid()));
    }

    private InvoiceStatus calculateInvoiceStatus(BigDecimal amountPaid, BigDecimal totalAmount) {
        if (amountPaid.compareTo(totalAmount) >= 0) return InvoiceStatus.PAID;
        if (amountPaid.compareTo(BigDecimal.ZERO) > 0) return InvoiceStatus.PARTIAL_PAID;
        return InvoiceStatus.UNPAID;
    }
}