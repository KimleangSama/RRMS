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
        invoice = updateInvoiceStatus(invoice, payment);
        payment.setInvoice(invoice);
        return PaymentResponse.fromPayment(user, payment);
    }

    @Transactional
    public List<PaymentResponse> getPaymentsOfInvoiceId(CustomUserDetails userDetails, UUID invoiceId) {
        User user = validateUser(userDetails);
        Invoice invoice = findAndValidateInvoice(invoiceId);
        validatePrivilege(user, invoice);
        return PaymentResponse.fromPayments(
                user,
                paymentRepository.findAllByInvoice(invoice)
        );
    }

    @Transactional
    public PaymentResponse editPayment(CustomUserDetails userDetails, UUID paymentId, EditPaymentInfoRequest request) {
        User user = validateUser(userDetails);
        Payment payment = findAndValidatePayment(paymentId);
        validatePrivilege(user, payment.getInvoice());
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

    private void validatePrivilege(User user, Invoice invoice) {
        if (PrivilegeChecker.isCreator(user, invoice.getCreatedBy()) ||
                PrivilegeChecker.isRoomOwner(user, invoice.getRoomAssignment().getRoom()) ||
                PrivilegeChecker.isPropertyOwner(user, invoice.getRoomAssignment().getRoom().getProperty()) ||
                PrivilegeChecker.isRoomAssignmentOwner(user, invoice.getRoomAssignment()) ||
                PrivilegeChecker.isRoomAssignmentTenant(user, invoice.getRoomAssignment())) {
            return;
        }
        throw new ResourceForbiddenException("Unauthorized to access this resource", invoice.getId());
    }

    private Payment createAndSavePayment(User user, Invoice invoice, CreatePaymentRequest request) {
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        PaymentMapper.mapToPayment(user, payment, request);
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created: {}", savedPayment);
        return savedPayment;
    }

    private Invoice updateInvoiceStatus(Invoice invoice, Payment payment) {
        double totalAmount = invoice.getTotalAmount();
        double amountDue = invoice.getAmountDue();
        double paymentAmountPaid = payment.getAmountPaid();
        double invoiceAmountPaid = invoice.getAmountPaid();

        invoice.setUpdatedBy(payment.getCreatedBy());
        invoice.setAmountPaid(invoiceAmountPaid + paymentAmountPaid);
        invoice.setAmountDue(totalAmount - invoice.getAmountPaid());
        invoice.setTotalAmount(totalAmount);
        invoice.setInvoiceStatus(getInvoiceStatus(amountDue));

        return invoiceRepository.save(invoice);
    }

    private InvoiceStatus getInvoiceStatus(double amountDue) {
        if (amountDue == 0) {
            return InvoiceStatus.PAID;
        } else if (amountDue > 0) {
            return InvoiceStatus.PARTIAL_PAID;
        } else {
            return InvoiceStatus.UNPAID;
        }
    }
}