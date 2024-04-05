package com.megapro.invoicesync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.megapro.invoicesync.dto.CustomerMapper;
import com.megapro.invoicesync.dto.InvoiceMapper;
import com.megapro.invoicesync.dto.ProductMapper;
import com.megapro.invoicesync.dto.request.CreateCustomerRequestDTO;
import com.megapro.invoicesync.dto.request.CreateInvoiceRequestDTO;
import com.megapro.invoicesync.repository.UserAppDb;
import com.megapro.invoicesync.service.CustomerService;
import com.megapro.invoicesync.service.InvoiceService;
import com.megapro.invoicesync.service.TaxService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.UUID;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.megapro.invoicesync.dto.response.ReadInvoiceResponse;
import com.megapro.invoicesync.model.Customer;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.model.Tax;
import com.megapro.invoicesync.model.UserApp;
import com.megapro.invoicesync.repository.InvoiceDb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Controller
public class InvoiceController {
    @Autowired
    InvoiceMapper invoiceMapper;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    UserAppDb userAppDb;

    @Autowired
    InvoiceDb invoiceDb;

    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    TaxService taxService;

    @GetMapping(value="/create-invoice")
    public String formCreateInvoice(Model model, @ModelAttribute("successMessage") String successMessage, 
                                    @ModelAttribute("errorMessage") String errorMessage){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();

        var invoiceDTO = new CreateInvoiceRequestDTO();
        invoiceDTO.setStaffEmail(email);
        var customerDTO = new CreateCustomerRequestDTO();
        List<Customer> listCustomer = customerService.getAllCustomer();

        System.out.println("GET INVOICE DATE "+invoiceDTO.getInvoiceDate());
        // List<Tax> listTax = taxService.getTaxes();

        model.addAttribute("email", email);
        model.addAttribute("role", role);
        // model.addAttribute("listTax", listTax);
        model.addAttribute("dateInvoice", invoiceService.parseDate(invoiceDTO.getInvoiceDate()));
        model.addAttribute("date",invoiceDTO.getInvoiceDate());
        model.addAttribute("status", invoiceDTO.getStatus());
        model.addAttribute("listCustomer", listCustomer);
        model.addAttribute("customerDTO", customerDTO);
        model.addAttribute("invoiceDTO", invoiceDTO);
        model.addAttribute("successMessage", successMessage);
        model.addAttribute("errorMessage", errorMessage);
        return "invoice/form-create-invoice";
    }

    @PostMapping(value = "/create-invoice")
    public String createInvoice(@Valid CreateInvoiceRequestDTO invoiceDTO, Model model,
                                RedirectAttributes redirectAttributes,
                                @RequestParam(value = "taxOption", required = false) List<Integer> selectedTaxIds,
                                // @RequestParam("image") MultipartFile signature,
                                @ModelAttribute("successMessage") String successMessage,
                                @ModelAttribute("errorMessage") String errorMessage) throws IOException{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();
        var customer = customerService.getCustomerById(invoiceDTO.getCustomerId());
        var invoice = invoiceMapper.createInvoiceRequestToInvoice(invoiceDTO);
        invoice.setCustomer(customer);
        invoiceService.attributeInvoice(invoice, selectedTaxIds);

        // byte[] imageBytes = signature.getBytes();
        // String bytesToString = invoiceService.translateByte(imageBytes);
        // invoiceDTO.setSignature(bytesToString);
        
        invoiceService.createInvoice(invoice, email);
        var newInvoiceDTO = new CreateInvoiceRequestDTO();
        model.addAttribute("email", email);
        model.addAttribute("role", role);
        model.addAttribute("invoiceDTO", newInvoiceDTO);
        model.addAttribute("successMessage", successMessage);
        model.addAttribute("errorMessage", errorMessage);
        redirectAttributes.addFlashAttribute("successMessage", "Invoice has been successfully created!");
        return "redirect:/create-invoice";
    }

    @GetMapping("/invoice/{invoiceNumber}")
    public String getDetailInvoice(@PathVariable("invoiceNumber") String encodedInvoiceNumber, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();
        String invoiceNumber = encodedInvoiceNumber.replace('_', '/');

        var invoice = invoiceService.getInvoiceByInvoiceNumber(invoiceNumber); // Perlu metode baru untuk mencari berdasarkan invoiceNumber
        List<Product> listProduct = invoiceService.getListProductInvoice(invoice);
        List<Tax> taxList = taxService.findAllTaxes();
        var invoiceDTO = invoiceMapper.readInvoiceToInvoiceResponse(invoice);

        model.addAttribute("status", invoice.getStatus());
        model.addAttribute("email", email);
        model.addAttribute("role", role);
        model.addAttribute("listProduct", listProduct);
        System.out.println("Ini tax "+ taxList.get(0));
        model.addAttribute("taxList", taxList);
        model.addAttribute("invoice", invoiceDTO);
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
        List<ReadInvoiceResponse> invoices = new ArrayList<>();

        for (Invoice invoice: myInvoices){
            var invoiceDTO = invoiceMapper.readInvoiceToInvoiceResponse(invoice);
            invoices.add(invoiceDTO);
        }
        
        // Mapping dari Invoice ke DTO jika diperlukan
        // List<ReadInvoiceResponse> myInvoiceDTOs = myInvoices.stream()
        //                                                     .map(invoice -> invoiceMapper.readInvoiceToInvoiceResponse(invoice))
        //                                                     .collect(Collectors.toList());
        model.addAttribute("invoices", invoices);
        return "invoice/my-invoices-view"; // Ganti dengan nama view Thymeleaf Anda
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

    @GetMapping("/invoice/{id}/edit")
    public String formUpdateCatalog(@PathVariable("id") UUID id, Model model, HttpServletRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();

        Invoice invoice = invoiceService.getInvoiceById(id);
        CreateInvoiceRequestDTO invoiceDTO = new CreateInvoiceRequestDTO();
        invoiceService.transferData(invoiceDTO, invoice);
        var listProduct = invoice.getListProduct();

        model.addAttribute("listProduct", listProduct);
        model.addAttribute("role", role);
        model.addAttribute("id", id);
        model.addAttribute("invoice", invoice);
        model.addAttribute("invoiceDTO", invoiceDTO);
        return "invoice/form-edit-invoice";
    }


}
