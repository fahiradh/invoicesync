package com.megapro.invoicesync.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.megapro.invoicesync.dto.InvoiceMapper;
import com.megapro.invoicesync.dto.request.CreateInvoiceRequestDTO;
import com.megapro.invoicesync.repository.UserAppDb;
import com.megapro.invoicesync.service.InvoiceService;

import java.util.UUID;
import org.springframework.web.bind.annotation.RequestParam;

import com.megapro.invoicesync.dto.response.ReadInvoiceResponse;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.model.UserApp;
import com.megapro.invoicesync.repository.InvoiceDb;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Controller
public class InvoiceController {
    @Autowired
    InvoiceMapper invoiceMapper;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    UserAppDb userAppDb;

    @Autowired
    InvoiceDb invoiceDb;

    @GetMapping(value="/create-invoice")
    public String getCreateInvoice(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();
        var invoiceDTO = new CreateInvoiceRequestDTO();
        model.addAttribute("role", role);
        model.addAttribute("invoiceDTO", invoiceDTO);
        return "invoice/form-create-invoice";
    }

    @PostMapping(value = "/create-invoice")
    public String createInvoice(@ModelAttribute CreateInvoiceRequestDTO invoiceDTO, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();
        var invoice = invoiceMapper.createInvoiceRequestToInvoice(invoiceDTO);
        invoiceService.attributeInvoce(invoice);
        invoiceService.createInvoice(invoice, email);

        model.addAttribute("role", role);
        model.addAttribute("invoice", invoice);
        return "invoice/success-create-invoice";
    }

    @GetMapping("/invoice/{id}")
    public String getDetailInvoice(@PathVariable("id") UUID invoiceId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();
        var invoice = invoiceService.getInvoiceById(invoiceId);
        List<Product> listProduct = invoiceService.getListProductInvoice(invoice);
        model.addAttribute("role", role);
        model.addAttribute("listProduct", listProduct);
        model.addAttribute("invoice", invoice);
        return "invoice/view-detail-invoice";
    }
    
    @GetMapping("/status/{status}")
    public String getInvoicesByStatus(@PathVariable String status, Model model) {
        List<Invoice> filteredInvoices = invoiceDb.findByStatus(status);
        model.addAttribute("invoices", filteredInvoices);
        return ""; // isi halaman view all invoice
    }

    @GetMapping("/invoices")
    public String getAllInvoices(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserApp user = userAppDb.findByEmail(email); // Assuming userAppDb is a service/repository for UserApp entities
        String role = user.getRole().getRole(); // Fetch the role of the user
        model.addAttribute("role", role);
        model.addAttribute("email", email);

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
        return "viewall-invoices";
    }

    @GetMapping(value="/invoices", params = "status")
    public String getAllInvoices(@RequestParam(value = "status") String status, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserApp user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();
        model.addAttribute("role", role);
        model.addAttribute("email", email);
        model.addAttribute("status", status);

        List<Invoice> invoiceList = invoiceService.retrieveInvoicesByStatus(status);
        List<ReadInvoiceResponse> invoiceDTOList = new ArrayList<>();

        for (Invoice invoice : invoiceList) {
            ReadInvoiceResponse invoiceDTO = invoiceMapper.readInvoiceToInvoiceResponse(invoice);
            
            // Fetch the staff user for each invoice to get their role
            UserApp invoiceUser = userAppDb.findByEmail(invoice.getStaffEmail());

            String staffRole = (invoiceUser != null) ? invoiceUser.getRole().getRole() : "Unknown Role";

            // Set the staff role into the invoice DTO
            invoiceDTO.setStaffRole(staffRole);
            invoiceDTOList.add(invoiceDTO);
        }

        model.addAttribute("invoices", invoiceDTOList);
        return "viewall-invoices";
    }

    @GetMapping("/my-invoices")
    public String getMyInvoices(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Mendapatkan email pengguna yang sedang login
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();

        model.addAttribute("email", email);
        model.addAttribute("role", role);
        // Asumsi Anda memiliki metode di InvoiceService untuk mengambil invoice berdasarkan email staff
        List<Invoice> myInvoices = invoiceService.retrieveInvoicesByEmail(email);
        
        // Mapping dari Invoice ke DTO jika diperlukan
        List<ReadInvoiceResponse> myInvoiceDTOs = myInvoices.stream()
                                                            .map(invoice -> invoiceMapper.readInvoiceToInvoiceResponse(invoice))
                                                            .collect(Collectors.toList());

        model.addAttribute("invoices", myInvoiceDTOs);
        return "my-invoices-view"; // Ganti dengan nama view Thymeleaf Anda
    }

    @GetMapping(value="/my-invoices", params = {"status"})
    public String getMyInvoices(@RequestParam(value = "status") String status, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Mendapatkan email pengguna yang sedang login
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();

        model.addAttribute("status", status);
        model.addAttribute("email", email);
        model.addAttribute("role", role);
        // Asumsi Anda memiliki metode di InvoiceService untuk mengambil invoice berdasarkan email staff
        List<Invoice> myInvoices = invoiceService.retrieveInvoicesByEmailAndStatus(email, status);
        
        // Mapping dari Invoice ke DTO jika diperlukan
        List<ReadInvoiceResponse> myInvoiceDTOs = myInvoices.stream()
                                                            .map(invoice -> invoiceMapper.readInvoiceToInvoiceResponse(invoice))
                                                            .collect(Collectors.toList());

        model.addAttribute("invoices", myInvoiceDTOs);
        return "my-invoices-view"; // Ganti dengan nama view Thymeleaf Anda
    }

    @GetMapping("/invoices/division/{division}")
    public String getInvoicesByDivision(@PathVariable("division") String requestedDivision, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Mendapatkan email pengguna yang sedang login
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();

        model.addAttribute("email", email);
        model.addAttribute("role", role);
        // Logika untuk mendapatkan role dan email pengguna yang terautentikasi sama
        
        // Tidak perlu memeriksa apakah role sesuai karena Anda sekarang bekerja berdasarkan divisi
        List<Invoice> invoiceList = invoiceService.retrieveInvoicesByDivision(requestedDivision);
        List<ReadInvoiceResponse> invoiceDTOList = invoiceList.stream()
                                                            .map(invoiceMapper::readInvoiceToInvoiceResponse)
                                                            .collect(Collectors.toList());

        model.addAttribute("invoices", invoiceDTOList);
        model.addAttribute("division", requestedDivision); // Ganti role dengan division
        
        return "viewall-invoices-division";
    }

    @GetMapping(value="/invoices/division/{division}", params = "status")
    public String getInvoicesByDivision(
            @PathVariable("division") String requestedDivision, 
            @RequestParam(value = "status") String status,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Mendapatkan email pengguna yang sedang login
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();

        model.addAttribute("email", email);
        model.addAttribute("role", role);
        model.addAttribute("status", status);

        List<Invoice> invoiceList = invoiceService.retrieveInvoicesByDivisionAndStatus(requestedDivision, status);
        List<ReadInvoiceResponse> invoiceDTOList = invoiceList.stream()
                                                            .map(invoiceMapper::readInvoiceToInvoiceResponse)
                                                            .collect(Collectors.toList());

        model.addAttribute("invoices", invoiceDTOList);
        model.addAttribute("division", requestedDivision);
        
        return "viewall-invoices-division";
    }

}
