package com.megapro.invoicesync.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

import com.megapro.invoicesync.dto.ApprovalMapper;
import com.megapro.invoicesync.dto.CustomerMapper;
import com.megapro.invoicesync.dto.FileMapper;
import com.megapro.invoicesync.dto.InvoiceMapper;
import com.megapro.invoicesync.dto.ProductMapper;
import com.megapro.invoicesync.dto.request.CreateCustomerRequestDTO;
import com.megapro.invoicesync.dto.request.CreateInvoiceRequestDTO;
import com.megapro.invoicesync.dto.request.UpdateInvoiceRequestDTO;
import com.megapro.invoicesync.repository.UserAppDb;
import com.megapro.invoicesync.service.ApprovalService;
import com.megapro.invoicesync.service.CustomerService;
import com.megapro.invoicesync.service.FilesStorageService;
import com.megapro.invoicesync.service.InvoiceService;
import com.megapro.invoicesync.service.TaxService;
import com.megapro.invoicesync.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.megapro.invoicesync.dto.response.ReadApprovalResponseDTO;
import com.megapro.invoicesync.dto.response.ReadFileResponseDTO;
import com.megapro.invoicesync.dto.response.ReadInvoiceResponse;
import com.megapro.invoicesync.model.Approval;
import com.megapro.invoicesync.model.Customer;
import com.megapro.invoicesync.model.Employee;
import com.megapro.invoicesync.model.FileModel;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.model.Tax;
import com.megapro.invoicesync.model.UserApp;
import com.megapro.invoicesync.repository.InvoiceDb;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.RequestBody;


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

    @Autowired
    UserService userService;

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private FilesStorageService fileService;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private ApprovalMapper approvalMapper;

    @GetMapping(value="/create-invoice")
    public String formCreateInvoice(Model model, @ModelAttribute("successMessage") String successMessage, 
                                    @ModelAttribute("errorMessage") String errorMessage){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();
        var invoiceDTO = new CreateInvoiceRequestDTO();
        invoiceDTO.setStaffEmail(email);
        invoiceDTO.setStatus("Draft");
        var customerDTO = new CreateCustomerRequestDTO();
        List<Customer> listCustomer = customerService.getAllCustomer();
        LocalDate date = invoiceDTO.getInvoiceDate();
        Employee employee = userService.findByEmail(email);

        model.addAttribute("email", email);
        model.addAttribute("role", role);
        model.addAttribute("dateInvoice", invoiceService.parseDate(invoiceDTO.getInvoiceDate()));
        model.addAttribute("date", String.format("%02d/%02d/%04d", date.getDayOfMonth(),  date.getMonth().getValue(), date.getYear()));
        model.addAttribute("status", invoiceDTO.getStatus());
        model.addAttribute("listCustomer", listCustomer);
        model.addAttribute("customerDTO", customerDTO);
        model.addAttribute("invoiceDTO", invoiceDTO);
        model.addAttribute("successMessage", successMessage);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("employee", employee);
        return "invoice/form-create-invoice";
    }

    @PostMapping(value = "/create-invoice")
    public String createInvoice(@Valid CreateInvoiceRequestDTO invoiceDTO, Model model,
                                RedirectAttributes redirectAttributes,
                                @RequestParam(value = "taxOption", required = false) List<Integer> selectedTaxIds,
                                @ModelAttribute("successMessage") String successMessage,
                                @ModelAttribute("errorMessage") String errorMessage,
                                @RequestParam("base64String") MultipartFile imageDataUrl,
                                @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();

        try{
            byte[] imageBytes = imageDataUrl.getBytes();
            String bytesToString = invoiceService.translateByte(imageBytes);
            invoiceDTO.setSignature(bytesToString);

            var message = invoiceService.checkValidity(invoiceDTO, selectedTaxIds, email).split(",");
            var newInvoiceDTO = new CreateInvoiceRequestDTO();

            if(!files[0].getOriginalFilename().equals("")){
                fileService.save(files, UUID.fromString(message[2]));
            }

            model.addAttribute("email", email);
            model.addAttribute("role", role);
            model.addAttribute("invoiceDTO", newInvoiceDTO);
            model.addAttribute("successMessage", successMessage);
            model.addAttribute("errorMessage", errorMessage);
            redirectAttributes.addFlashAttribute(message[0], message[1]);
        } catch (IOException e){
            String mess = "Failed";
            redirectAttributes.addFlashAttribute("errorMessage", mess);
        }
        return "redirect:/create-invoice";
    }

    @GetMapping("/invoice/{invoiceNumber}")
    public String getDetailInvoice(@PathVariable("invoiceNumber") String encodedInvoiceNumber, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();
        String invoiceNumber = encodedInvoiceNumber.replace('_', '/');

        var invoice = invoiceService.getInvoiceByInvoiceNumber(invoiceNumber);
        List<Product> listProduct = invoiceService.getListProductInvoice(invoice);
        List<Tax> taxList = taxService.findAllTaxes();
        var invoiceDTO = invoiceMapper.readInvoiceToInvoiceResponse(invoice);
        List<UserApp> employees = userAppDb.findByRoleName("Finance Staff");
        List<Approval> approvers = approvalService.findApproversByInvoice(invoice);
        model.addAttribute("approvers", approvers);
        // Replace "ApproverRole" with the actual role name
        model.addAttribute("employees", employees);
        var date = invoiceDTO.getInvoiceDate();
        Employee employee = userService.findByEmail(email);

        var emailPermission = email.equals(invoice.getStaffEmail());

        model.addAttribute("image", invoice.getSignature());
        model.addAttribute("status", invoice.getStatus());
        model.addAttribute("email", email);
        model.addAttribute("role", role);
        model.addAttribute("listProduct", listProduct);
        model.addAttribute("date", String.format("%02d/%02d/%04d", date.getDayOfMonth(),  date.getMonth().getValue(), date.getYear()));
        model.addAttribute("taxList", taxList);
        model.addAttribute("invoice", invoiceDTO);
        model.addAttribute("dateInvoice", invoiceService.parseDate(invoiceDTO.getInvoiceDate()));
        model.addAttribute("employee", employee);
        model.addAttribute("emailPermission", emailPermission);

        // Bagian logs
        var approvals = invoice.getListApproval();
        List<ReadApprovalResponseDTO> approvalLogs = new ArrayList<>();
        for(Approval approval:approvals){
            if(approval.getApprovalStatus()==null || approval.getApprovalStatus().isEmpty()){
                break;
            }
            var filesLog = approval.getApprovalFiles();
            List<ReadFileResponseDTO> filesDTO = new ArrayList<>();
            if(filesLog != null || filesLog.size()!=0){
                for(FileModel fileModel : filesLog){
                    var fileDTO = fileMapper.fileModelToReadFileResponseDTO(fileModel);
                    filesDTO.add(fileDTO);
                }
            }
            var approvalLog = approvalMapper.approvalToReadApprovalResponseDTO(approval);
            approvalLog.setFilesDTO(filesDTO);
            approvalLogs.add(approvalLog);
        }

        model.addAttribute("approvalLogs", approvalLogs);

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
        return "invoice/viewall-invoices";
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
        return "invoice/viewall-invoices";
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
        
        return "invoice/viewall-invoices-division";
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
        
        return "invoice/viewall-invoices-division";
    }

    @GetMapping("/invoice/{invoiceNumber}/edit")
    public String formEditInvoice(@PathVariable("invoiceNumber") String encodedInvoiceNumber,
                                    Model model, HttpServletRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();
        String invoiceNumber = encodedInvoiceNumber.replace('_', '/');

        var invoice = invoiceService.getInvoiceByInvoiceNumber(invoiceNumber);
        UpdateInvoiceRequestDTO invoiceDTO = invoiceMapper.updateInvoiceToInvoiceDTO(invoice);
        List<Product> listProduct = invoiceService.getListProductInvoice(invoice);
        var date = invoice.getInvoiceDate();
        Employee employee = userService.findByEmail(email);

        model.addAttribute("image", invoice.getSignature());
        model.addAttribute("listProduct", listProduct);
        model.addAttribute("role", role);
        model.addAttribute("date", String.format("%02d/%02d/%04d", date.getDayOfMonth(),  date.getMonth().getValue(), date.getYear()));
        model.addAttribute("invoiceNumber", invoiceNumber);
        model.addAttribute("dateInvoice", invoiceService.parseDate(invoice.getInvoiceDate()));
        model.addAttribute("customer", invoice.getCustomer());
        model.addAttribute("email", email);
        model.addAttribute("invoiceDTO", invoiceDTO);
        model.addAttribute("currentSignature", invoice.getSignature());
        model.addAttribute("employee", employee);
        return "invoice/form-edit-invoice";
    }

    @PostMapping("/invoice/edit")
    public String editInvoice(@ModelAttribute UpdateInvoiceRequestDTO invoiceDTO, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();
        var invoiceFromDTO = invoiceMapper.updateInvoiceDTOToInvoice(invoiceDTO);
        var invoice = invoiceService.updateInvoice(invoiceFromDTO);
        String encodedInvoiceNumber = invoice.getInvoiceNumber().replace("/", "_");
        model.addAttribute("email", email);
        model.addAttribute("role", role);
        return String.format("redirect:/invoice/%s", encodedInvoiceNumber);
    }

}
