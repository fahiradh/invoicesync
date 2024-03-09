package com.megapro.invoicesync.service;

import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;

import java.util.List;
import java.util.UUID;

import com.megapro.invoicesync.dto.response.ReadInvoiceResponse;

public interface InvoiceService {
    void createInvoice(Invoice invoice, String email);
    void attributeInvoce(Invoice invoice);
    long countInvoice();
    Invoice getInvoiceById(UUID id);
    List<Invoice> retrieveAllInvoice();
    List<Invoice> getInvoiceByStaffEmail(String email);
    List<Product> getListProductInvoice(Invoice invoice);
    List<Invoice> retrieveInvoicesByRole(String role);
    List<Invoice> retrieveInvoicesByEmail(String email);
    List<Invoice> retrieveInvoicesByEmailAndStatus(String email, String status);
    
}
