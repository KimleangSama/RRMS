package com.kkimleang.rrms.repository.payment;

import com.kkimleang.rrms.entity.Invoice;
import com.kkimleang.rrms.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
}
