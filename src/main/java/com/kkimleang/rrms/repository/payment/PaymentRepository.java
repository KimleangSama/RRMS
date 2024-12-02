package com.kkimleang.rrms.repository.payment;

import com.kkimleang.rrms.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findAllByInvoice(Invoice invoice);
}
