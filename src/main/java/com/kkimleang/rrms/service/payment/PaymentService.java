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
    public PaymentResponse createPayment(CustomUserDetails user, CreatePaymentRequest request) {
        // Validate inputs
        User validUser = validateUser(user);
        Invoice invoice = findAndValidateInvoice(request.getInvoiceId());
        // Create and save payment
        Payment payment = createAndSavePayment(validUser, invoice, request);
        // Update invoice status
        updateInvoiceStatus(invoice, payment, validUser.getId());
        return PaymentResponse.fromPayment(validUser, payment);
    }

    private User validateUser(CustomUserDetails user) {
        User validUser = user.getUser();
        NullOrDeletedEntityValidator.validate(validUser, "User");
        return validUser;
    }

    private Invoice findAndValidateInvoice(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));
        NullOrDeletedEntityValidator.validate(invoice, "Invoice");
        NullOrDeletedEntityValidator.validate(invoice.getRoomAssignment(), "Room Assignment");
        return invoice;
    }

    private Payment createAndSavePayment(User user, Invoice invoice, CreatePaymentRequest request) {
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        PaymentMapper.mapToPayment(user, payment, request);
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created: {}", savedPayment);
        return savedPayment;
    }

    private void updateInvoiceStatus(Invoice invoice, Payment payment, UUID userId) {
        invoice.setUpdatedAt(Instant.now());
        invoice.setUpdatedBy(userId);
        BigDecimal newAmountPaid = BigDecimal.valueOf(invoice.getAmountPaid()).add(BigDecimal.valueOf(payment.getAmountPaid()));
        invoice.setAmountPaid(newAmountPaid.doubleValue());
        invoice.setAmountDue(invoice.getTotalAmount() - invoice.getAmountPaid());
        InvoiceStatus newStatus = calculateInvoiceStatus(newAmountPaid, BigDecimal.valueOf(invoice.getTotalAmount()));
        invoice.setInvoiceStatus(newStatus);
        invoiceRepository.save(invoice);
    }

    private InvoiceStatus calculateInvoiceStatus(BigDecimal amountPaid, BigDecimal totalAmount) {
        if (amountPaid.compareTo(totalAmount) >= 0) {
            return InvoiceStatus.PAID;
        } else if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
            return InvoiceStatus.PARTIAL_PAID;
        }
        return InvoiceStatus.UNPAID;
    }

    @Transactional
    public List<PaymentResponse> getPaymentsOfInvoiceId(CustomUserDetails user, UUID invoiceId) {
        User validUser = validateUser(user);
        Invoice invoice = findAndValidateInvoice(invoiceId);
        List<Payment> payments = paymentRepository.findAllByInvoice(invoice);
        return PaymentResponse.fromPayments(validUser, payments);
    }
}
