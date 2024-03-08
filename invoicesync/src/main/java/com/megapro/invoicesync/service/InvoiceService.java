package com.megapro.invoicesync.service;

import java.util.List;

import com.megapro.invoicesync.dto.response.ReadInvoiceResponse;
import com.megapro.invoicesync.model.Invoice;
import java.util.List;
import java.util.UUID;

public interface InvoiceService {
    void createInvoice(Invoice invoice, String email);
    void attributeInvoce(Invoice invoice);
    long countInvoice();
    Invoice getInvoiceById(UUID id);
    List<Invoice> retrieveAllInvoice();
    List<Invoice> retrieveInvoicesByRole(String role);
    List<Invoice> retrieveInvoicesByEmail(String email);
    
}
