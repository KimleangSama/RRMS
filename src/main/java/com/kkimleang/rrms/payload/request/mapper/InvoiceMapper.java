package com.kkimleang.rrms.payload.request.mapper;

import com.kkimleang.rrms.config.*;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.payload.request.payment.*;
import lombok.extern.slf4j.*;

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
        invoice.setInvoiceStatus(request.getInvoiceStatus());
    }

    public static void editInvoiceStatusFromEditInvoiceStatusRequest(Invoice invoice, EditInvoiceInfoRequest request) {
        ModelMapperConfig.modelMapper().map(request, invoice);
    }
}
