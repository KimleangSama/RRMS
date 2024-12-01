package com.kkimleang.rrms.payload.request.mapper;

import com.kkimleang.rrms.entity.Invoice;
import com.kkimleang.rrms.payload.request.payment.CreateInvoiceRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvoiceMapper {
    public static void createInvoiceFromInvoiceRequest(
            Invoice invoice,
            CreateInvoiceRequest request
    ) {
        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setAmountPaid(request.getAmountPaid());
        invoice.setAmountDue(request.getAmountDue());
        invoice.setDiscount(request.getDiscount());
        invoice.setRemark(request.getRemark());
        invoice.setTotalAmount(request.getAmountDue() - request.getDiscount() - request.getAmountPaid());
        invoice.setInvoiceStatus(request.getInvoiceStatus());
    }
}
