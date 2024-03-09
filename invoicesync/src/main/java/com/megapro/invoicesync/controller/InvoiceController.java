package com.megapro.invoicesync.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.megapro.invoicesync.dto.InvoiceMapper;
// import com.megapro.invoicesync.dto.request.CreateInvoiceRequest;
import com.megapro.invoicesync.dto.response.ReadInvoiceResponse;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.dto.request.CreateInvoiceRequestDTO;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.model.UserApp;
import com.megapro.invoicesync.repository.UserAppDb;
import com.megapro.invoicesync.dto.request.CreateInvoiceRequestDTO;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.repository.InvoiceDb;
import com.megapro.invoicesync.service.InvoiceService;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class InvoiceController {
    @Autowired
    InvoiceMapper invoiceMapper;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceDb invoiceDb;

    @Autowired
    UserAppDb userAppDb;

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
        var invoice = invoiceMapper.createInvoiceRequestToInvoice(invoiceDTO);
        invoiceService.attributeInvoce(invoice);
        invoiceService.createInvoice(invoice, email);
        model.addAttribute("invoice", invoice);
        return "invoice/success-create-invoice";
    }

    @GetMapping("/invoice/{invoiceId}")
    public String getDetailInvoice(@PathVariable("invoiceId") UUID invoiceId, Model model) {
        var invoice = invoiceService.getInvoiceById(invoiceId);
        var invoiceDTO = invoiceMapper.readInvoiceToInvoiceResponse(invoice);
        model.addAttribute("invoice", invoiceDTO);
        return "invoice/view-detail-invoice";
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
            
            // Fetch the staff user for each invoice to get their role
            UserApp invoiceUser = userAppDb.findByEmail(invoice.getStaffEmail());
            String staffRole = invoiceUser.getRole().getRole();

            // Set the staff role into the invoice DTO
            invoiceDTO.setStaffRole(staffRole); // Make sure ReadInvoiceResponse has a field for staffRole
            invoiceDTOList.add(invoiceDTO);
        }

        model.addAttribute("invoices", invoiceDTOList);
        return "viewall-invoices";
    }

    @GetMapping("/invoices/{role}")
    public String getInvoicesByRole(@PathVariable("role") String requestedRole, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserApp currentUser = userAppDb.findByEmail(email);
        String currentUserRole = currentUser.getRole().getRole();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();

        model.addAttribute("email", email);
        model.addAttribute("role", role);
        
        // Check if the current user's role matches the requested role.
        // You may also want to check if the current user has permissions to view invoices from other roles.
        if(!currentUserRole.equalsIgnoreCase(requestedRole)) {
            // Handle unauthorized access, perhaps by showing an error message or redirecting to another page
            return "error/unauthorized"; // Replace with your actual error page view name
        }

        // If the user has permission, proceed to retrieve invoices for that role
        List<Invoice> invoiceList = invoiceService.retrieveInvoicesByRole(requestedRole);
        List<ReadInvoiceResponse> invoiceDTOList = invoiceList.stream()
                                                            .map(invoiceMapper::readInvoiceToInvoiceResponse)
                                                            .collect(Collectors.toList());
        model.addAttribute("invoices", invoiceDTOList);
        model.addAttribute("role", requestedRole); // Add the role to the model if you need to display it in the view
        return "viewall-invoices-division"; // Replace with the actual view name for the invoices list
    }

    @GetMapping(value="/invoices/{role}", params = {"status"})
    public String getInvoicesByRole(@PathVariable("role") String requestedRole, @RequestParam(value = "status") String status, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserApp currentUser = userAppDb.findByEmail(email);
        String currentUserRole = currentUser.getRole().getRole();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();

        model.addAttribute("email", email);
        model.addAttribute("role", role);
        
        // Check if the current user's role matches the requested role.
        // You may also want to check if the current user has permissions to view invoices from other roles.
        if(!currentUserRole.equalsIgnoreCase(requestedRole)) {
            // Handle unauthorized access, perhaps by showing an error message or redirecting to another page
            return "error/unauthorized"; // Replace with your actual error page view name
        }

        // If the user has permission, proceed to retrieve invoices for that role
        List<Invoice> invoiceList = invoiceService.retrieveInvoicesByRole(requestedRole);
        List<ReadInvoiceResponse> invoiceDTOList = invoiceList.stream()
                                                            .map(invoiceMapper::readInvoiceToInvoiceResponse)
                                                            .collect(Collectors.toList());
        model.addAttribute("invoices", invoiceDTOList);
        model.addAttribute("role", requestedRole); // Add the role to the model if you need to display it in the view
        return "viewall-invoices-division"; // Replace with the actual view name for the invoices list
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

}