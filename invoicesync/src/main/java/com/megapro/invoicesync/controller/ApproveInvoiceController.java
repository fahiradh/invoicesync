package com.megapro.invoicesync.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.megapro.invoicesync.dto.InvoiceMapper;
import com.megapro.invoicesync.dto.response.ReadInvoiceResponse;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.model.UserApp;
import com.megapro.invoicesync.repository.UserAppDb;
import com.megapro.invoicesync.service.InvoiceService;


@Controller
public class ApproveInvoiceController {

    @Autowired
    UserAppDb userAppDb;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceMapper invoiceMapper;
    
    @GetMapping("/approve-invoice")
    public String approveInvoicePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();

        model.addAttribute("role", role);
        System.out.println("INI ROLE");
        System.out.println(role);

        List<Invoice> invoiceList = invoiceService.retrieveAllInvoice();
        List<ReadInvoiceResponse> invoiceDTOList = new ArrayList<>();

        for (Invoice invoice : invoiceList) {
            ReadInvoiceResponse invoiceDTO = invoiceMapper.readInvoiceToInvoiceResponse(invoice);
            
            UserApp invoiceUser = userAppDb.findByEmail(invoice.getStaffEmail());
            
            // Cek apakah invoiceUser adalah null
            String staffRole = (invoiceUser != null) ? invoiceUser.getRole().getRole() : "Unknown Role";
        
            invoiceDTO.setStaffRole(staffRole);
            invoiceDTOList.add(invoiceDTO);
        }
        model.addAttribute("invoices", invoiceDTOList);
        model.addAttribute("email", email);
        return "approve-invoice/list-approval.html";
    }
    
    // // Detail invoice untuk diapprove
    // @GetMapping("/approve-invoice/{id}")
    // public String getApprovalDetail(@PathVariable("id") UUID invoiceId, Model model) {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     String email = authentication.getName();
    //     var user = userAppDb.findByEmail(email);
    //     String role = user.getRole().getRole();
    //     var invoice = invoiceService.getInvoiceById(invoiceId);
    //     List<Product> listProduct = invoiceService.getListProductInvoice(invoice);
    //     var invoiceDTO = invoiceMapper.readInvoiceToInvoiceResponse(invoice);

    //     model.addAttribute("status", invoice.getStatus());
    //     model.addAttribute("email", email);
    //     model.addAttribute("role", role);
    //     model.addAttribute("listProduct", listProduct);
    //     model.addAttribute("invoice", invoiceDTO);

    //     return "approve-invoice/approval-page.html";
    // }
    
}
