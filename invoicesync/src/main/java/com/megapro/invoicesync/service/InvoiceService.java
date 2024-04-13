package com.megapro.invoicesync.service;

import com.megapro.invoicesync.dto.request.CreateInvoiceRequestDTO;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

public interface InvoiceService {
    void createInvoice(Invoice invoice, String email);
    void attributeInvoice(Invoice invoice, List<Integer> listTax);
    long countInvoice();
    Invoice getInvoiceById(UUID id);
    List<Invoice> retrieveAllInvoice();
    List<Invoice> retrieveInvoicesByRole(String role);
    List<Invoice> retrieveInvoicesByEmail(String email);
    List<Invoice> retrieveInvoicesByDivision(String division);
    List<Invoice> getInvoiceByStaffEmail(String email);
    List<Product> getListProductInvoice(Invoice invoice);
    List<Invoice> retrieveInvoicesByEmailAndStatus(String email, String status);
    List<Invoice> retrieveInvoicesByDivisionAndStatus(String division, String status);
    List<Invoice> retrieveInvoicesByStatus(String status);
    Invoice getDummyInvoice();
    String translateByte(byte[] byteFile);
    // void transferData(CreateInvoiceRequestDTO invoiceRequestDTO, Invoice invoice);
    Invoice getInvoiceByInvoiceNumber(String invoiceNumber);
    String parseDate(LocalDate localDate);
    String checkValidity(CreateInvoiceRequestDTO invoiceDTO, List<Integer> selectedTaskIds, String email);
    Invoice updateInvoice(Invoice invoiceFromDTO);
    void addApproverToInvoice(UUID invoiceId, String email);
}
