package com.megapro.invoicesync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.megapro.invoicesync.dto.InvoiceMapper;
import com.megapro.invoicesync.dto.request.CreateInvoiceRequestDTO;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.repository.InvoiceDb;
import com.megapro.invoicesync.service.InvoiceService;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

@Controller
public class InvoiceController {
    @Autowired
    InvoiceMapper invoiceMapper;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceDb invoiceDb;

    @GetMapping(value="/create-invoice")
    public String getCreateInvoice(Model model){
        var invoiceDTO = new CreateInvoiceRequestDTO();
        model.addAttribute("invoiceDTO", invoiceDTO);
        return "invoice/form-create-invoice";
    }

    @PostMapping(value = "/create-invoice")
    public String createInvoice(@ModelAttribute CreateInvoiceRequestDTO invoiceDTO, Model model){
        var invoice = invoiceMapper.createInvoiceRequestToInvoice(invoiceDTO);
        invoiceService.attributeInvoce(invoice);
        invoiceService.createInvoice(invoice);
        model.addAttribute("invoice", invoice);
        return "invoice/success-create-invoice";
    }
    
    @GetMapping("/status/{status}")
    public String getInvoicesByStatus(@PathVariable String status, Model model) {
        List<Invoice> filteredInvoices = invoiceDb.findByStatus(status);
        model.addAttribute("invoices", filteredInvoices);
        return ""; // isi halaman view all invoice
    }
}
