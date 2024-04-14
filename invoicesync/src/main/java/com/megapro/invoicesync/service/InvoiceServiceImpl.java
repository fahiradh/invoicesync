package com.megapro.invoicesync.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megapro.invoicesync.dto.InvoiceMapper;
import com.megapro.invoicesync.dto.request.CreateInvoiceRequestDTO;
import com.megapro.invoicesync.dto.response.ApproverDisplay;
import com.megapro.invoicesync.model.Approval;
import com.megapro.invoicesync.model.ApprovalFlow;
import com.megapro.invoicesync.model.Employee;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.model.Tax;
import com.megapro.invoicesync.model.UserApp;
import com.megapro.invoicesync.repository.ApprovalDb;
import com.megapro.invoicesync.repository.ApprovalFlowDb;
import com.megapro.invoicesync.repository.EmployeeDb;
import com.megapro.invoicesync.repository.InvoiceDb;
import com.megapro.invoicesync.repository.ProductDb;
import com.megapro.invoicesync.repository.TaxDb;
import com.megapro.invoicesync.repository.UserAppDb;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Map;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService{
    @Autowired
    InvoiceDb invoiceDb;

    @Autowired
    ProductService productService;

    @Autowired
    ProductDb productDb;

    @Autowired
    TaxDb taxDb;

    @Autowired
    UserAppDb userAppDb;

    @Autowired
    EmployeeDb employeeDb;

    @Autowired
    ApprovalDb approvalDb;

    @Autowired
    CustomerService customerService;

    @Autowired
    InvoiceMapper invoiceMapper;

    @Autowired
    ApprovalFlowDb approvalFlowDb;

    @Override
    public void createInvoice(Invoice invoice, String email) {
        invoice.setStaffEmail(email);
        invoiceDb.save(invoice);
    }

    @Override
    public long countInvoice(){
        return invoiceDb.count();
    }

    @Override
    public void attributeInvoice(Invoice invoice, List<Integer> listTax) {
        long count = countInvoice();
        String countStr = String.format("%04d", count);
        var invoiceDate = invoice.getInvoiceDate();
        int month = invoiceDate.getMonthValue();
        String monthInRoman = convertToRoman(month);
        String year = String.valueOf(invoiceDate.getYear());
        String invoiceNumber = String.format("INV-%s/Krida/%s/%s",countStr,monthInRoman,year);
        invoice.setInvoiceNumber(invoiceNumber);
        var dummy = getDummyInvoice();
        List<Product> newListProduct = new ArrayList<>();
        List<Product> listProduct = productService.getAllProductDummyInvoice(dummy);
        for (Product p : listProduct) {
            p.setInvoice(invoice);
            newListProduct.add(p);
        }
        invoice.setListProduct(newListProduct);
        invoice.setListTax(taxDb.findByTaxIdIn(listTax));
        calculateSubtotal(invoice);
        calculateDiscount(invoice);
        calculateTax(invoice);
        calculateGrandTotal(invoice);
    }

    @Override
    public String translateByte(byte[] byteFile){
        return Base64.getEncoder().encodeToString(byteFile);
    }

    public static String convertToRoman(int num) {
        int[] VALUES = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
        String[] NUMERALS = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };

        StringBuilder romanNumeral = new StringBuilder();
        int i = 0;
        while (num > 0) {
            if (num - VALUES[i] >= 0) {
                romanNumeral.append(NUMERALS[i]);
                num -= VALUES[i];
            } else {
                i++;
            }
        }
        return romanNumeral.toString();
    }

    private void calculateSubtotal(Invoice invoice){
        double total = 0;
        for (Product p : invoice.getListProduct()){
            total += p.getTotalPrice().doubleValue();
        }
        invoice.setSubtotal(BigDecimal.valueOf(total));
    }

    private void calculateDiscount(Invoice invoice){
        double subtotal = invoice.getSubtotal().doubleValue();
        double discount = (invoice.getTotalDiscount()/100.0)*subtotal;
        double afterDiscount = subtotal-discount;
        invoice.setGrandTotal(BigDecimal.valueOf(afterDiscount));
    }

    private void calculateTax(Invoice invoice){
        double total = 0;
        for(Tax tax:invoice.getListTax()){
            total += (tax.getTaxPercentage()*invoice.getGrandTotal().doubleValue()/100);
        }
        invoice.setTaxTotal(BigDecimal.valueOf(total));
    }

    private void calculateGrandTotal(Invoice invoice){
        BigDecimal total = invoice.getGrandTotal().add(invoice.getTaxTotal());
        invoice.setGrandTotal(total);
    }

    @Override
    public List<Invoice> retrieveAllInvoice() {
        return invoiceDb.findAll();
    }

    @Override
    public Invoice getInvoiceById(UUID id){
        for (Invoice inv : retrieveAllInvoice()) {
            if (inv.getInvoiceId().equals(id)) {
                return inv;
            }
        }
        return null;
    }

    @Override
    public List<Invoice> getInvoiceByStaffEmail(String email) {
        return invoiceDb.findByStaffEmail(email);
    }

    @Override
    public List<Product> getListProductInvoice(Invoice invoice) {
        List<Product> listProduct = productService.getAllProduct();
        List<Product> listProductInInvoice = new ArrayList<>();
        for (Product p: listProduct){
            if (p.getInvoice().getInvoiceId().equals(invoice.getInvoiceId())){
                listProductInInvoice.add(p);
            }
        }
        return listProductInInvoice;
    }

    @Override
    public List<Invoice> retrieveInvoicesByRole(String role) {
        List<UserApp> usersInRole = userAppDb.findByRoleName(role);
        List<String> emailsInRole = usersInRole.stream()
                                               .map(UserApp::getEmail)
                                               .collect(Collectors.toList());
        return invoiceDb.findByStaffEmailIn(emailsInRole);
    }

    @Override
    public List<Invoice> retrieveInvoicesByEmail(String email) {
        // Asumsi Anda memiliki metode di InvoiceRepository untuk mengambil invoice berdasarkan email staff
        return invoiceDb.findByStaffEmail(email);
    }

    @Override
    public List<Invoice> retrieveInvoicesByDivision(String division) {
        // Ambil semua user dari divisi tertentu
        List<UserApp> usersInDivision = userAppDb.findAll().stream()
                                                  .filter(user -> user.getRole().getRole().contains(division))
                                                  .collect(Collectors.toList());
        
        // Ambil email dari semua user ini
        List<String> emailsInDivision = usersInDivision.stream()
                                                        .map(UserApp::getEmail)
                                                        .collect(Collectors.toList());
        
        // Gunakan email ini untuk menemukan semua invoice yang terkait
        return invoiceDb.findByStaffEmailIn(emailsInDivision);
    }
    
    
    
    public List<Invoice> retrieveInvoicesByEmailAndStatus(String email, String status) {
        return invoiceDb.findByStaffEmailAndStatus(email, status);
    }

    @Override
    public List<Invoice> retrieveInvoicesByDivisionAndStatus(String division, String status) {
        return invoiceDb.findByEmployeeRoleNameAndStatus(division, status);
    }

    @Override
    public List<Invoice> retrieveInvoicesByStatus(String status) {
        return invoiceDb.findByStatus(status);
    }

    @Override
    public Invoice getDummyInvoice() {
        return invoiceDb.findDummyInvoice();
    }

    @Override
    public Invoice getInvoiceByInvoiceNumber(String invoiceNumber) {
        Optional<Invoice> invoice = invoiceDb.findByInvoiceNumber(invoiceNumber);
        return invoice.orElseThrow(() -> new EntityNotFoundException("Invoice with number: " + invoiceNumber + " was not found."));
    }
    
    @Override
    public String parseDate(LocalDate localDate){
        int day = localDate.getDayOfMonth();
        int monthIndex = localDate.getMonthValue();
        int year = localDate.getYear();
        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                               "July", "August", "September", "October", "November", "December"};
        String formattedDate = day + " " + monthNames[monthIndex] + " " + year;
        return formattedDate;
    }

    @Override
    public void addApproverToInvoice(UUID invoiceId, String approverEmail) {
        Invoice invoice = invoiceDb.findById(invoiceId)
            .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));
        
        Employee employee = employeeDb.findByEmail(approverEmail);
    
        if(approvalDb.existsByInvoiceAndEmployee(invoice, employee)) {
            throw new IllegalStateException("Employee already added as an approver for this invoice.");
        }
        
        List<ApprovalFlow> approvalFlows = approvalFlowDb.findAllByOrderByNominalRangeDesc();
        
        ApprovalFlow requiredFlow = approvalFlows.stream()
            .filter(flow -> invoice.getGrandTotal().compareTo(BigDecimal.valueOf(flow.getNominalRange())) >= 0)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No approval flow matches the invoice's nominal range."));
    
        long existingApprovalsCount = approvalDb.countByInvoice(invoice);
        if (existingApprovalsCount >= requiredFlow.getApprovalRank()) {
            throw new IllegalStateException("The number of approvers for this invoice has reached the limit for the required rank.");
        }
        
        
        Approval approval = new Approval();
        approval.setEmployee(employee);
        approval.setInvoice(invoice);
        approval.setApprovalStatus("Pending"); // Set the initial approval status
        approval.setApprovalTime(LocalDate.now());
        
        // Set rank of the approval based on the required flow
        approval.setRank(requiredFlow.getApprovalRank());
        if(approval.getRank() == 1) {
            approval.setShown(true);
        }
        approvalDb.save(approval);
    }
    
    
//     @Override
// public void addApproverToInvoice(UUID invoiceId, String approverEmail) {
//     Invoice invoice = invoiceDb.findById(invoiceId)
//         .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));

//     Employee employee = employeeDb.findByEmail(approverEmail);
//     if(employee == null) {
//         throw new EntityNotFoundException("Employee with email: " + approverEmail + " not found");
//     }

//     if(approvalDb.existsByInvoiceAndEmployee(invoice, employee)) {
//         throw new IllegalStateException("Employee already added as an approver for this invoice.");
//     }

//     BigDecimal grandTotal = invoice.getGrandTotal();
//     ApprovalFlow requiredFlow = approvalFlowDb
//         .findTopByNominalRangeLessThanEqualOrderByNominalRangeDesc(grandTotal)
//         .orElseThrow(() -> new IllegalArgumentException("No approval flow matches the invoice's nominal range."));

//     if (!employee.getRole().equals(requiredFlow.getApproverRole())) {
//         throw new IllegalStateException("Employee does not have the required role for this approval rank.");
//     }

//     long existingApprovalsCount = approvalDb.countByInvoice(invoice);
//     if(existingApprovalsCount >= requiredFlow.getApprovalRank()) {
//         throw new IllegalStateException("The number of approvers for this invoice has reached the limit for the required rank.");
//     }

//     Approval approval = new Approval();
//     approval.setEmployee(employee);
//     approval.setInvoice(invoice);
//     approval.setApprovalStatus("Pending");
//     approval.setRank((int) (existingApprovalsCount + 1)); // Increment the rank based on existing approvals
    
//     approvalDb.save(approval);
// }

    
    public String checkValidity(CreateInvoiceRequestDTO invoiceDTO, List<Integer> selectedTaxIds, String email) {
        var res = "";
        if (invoiceDTO.getCustomerId() == null){
            res = "errorMessage, Customer can't be empty";
        }
        else if (invoiceDTO.getAccountName() == null || invoiceDTO.getAccountNumber() == null ||
                invoiceDTO.getBankName() == null){
            res = "errorMessage, Please provide the account information";
        }
        else if (invoiceDTO.getDueDate() == null){
            res = "errorMessage, Please select invoice due date";
        }
        else {
            invoiceDTO.setStatus("Waiting for Approver");
            System.out.println("customer id nya adalah " + invoiceDTO.getCustomerId());
            var customer = customerService.getCustomerById(invoiceDTO.getCustomerId());
            System.out.println("total discount " + invoiceDTO.getTotalDiscount());
            var invoice = invoiceMapper.createInvoiceRequestToInvoice(invoiceDTO);
            invoice.setCustomer(customer);
            attributeInvoice(invoice, selectedTaxIds);
            // invoiceDTO.setSignature(imageDataUrl);
            createInvoice(invoice, email);
            res = "successMessage, Invoice created successfully!," + invoice.getInvoiceId();
        }    
        return res;
    }

    // ... other methods in InvoiceServiceImpl ...
@Override
public List<ApproverDisplay> getApproverDisplaysForInvoice(Invoice invoice) {
    BigDecimal total = invoice.getGrandTotal();
    List<ApprovalFlow> approvalFlows = approvalFlowDb.findAllByOrderByNominalRangeDesc();
    List<String> rolesToFind = new ArrayList<>();

    // Determine which roles are required based on the total price and approval flow
    for (ApprovalFlow flow : approvalFlows) {
        if (total.compareTo(BigDecimal.valueOf(flow.getNominalRange())) >= 0) {
            rolesToFind.add(flow.getApproverRole());
            if (rolesToFind.size() == flow.getApprovalRank()) {
                break;
            }
        }
    }

    // Fetch employees based on the roles determined above
    List<UserApp> employees = userAppDb.findByRoleNames(rolesToFind);
    
    // Fetch existing approvers for this invoice
    List<Approval> existingApprovers = approvalDb.findByInvoice(invoice);

    // Map existing approvers to their roles for display
    Map<String, List<Approval>> approversByRole = existingApprovers.stream()
        .collect(Collectors.groupingBy(approval -> approval.getEmployee().getRole().getRole()));

    // Prepare the list of approvers for each required role
    return rolesToFind.stream()
        .map(role -> new ApproverDisplay(role, approversByRole.getOrDefault(role, new ArrayList<>())))
        .collect(Collectors.toList());
}

// ... rest of the service implementation ...


    @Override
    public Invoice updateInvoice(Invoice invoiceFromDTO) {
        Invoice invoice = getInvoiceById(invoiceFromDTO.getInvoiceId());
        if (invoice != null){
            invoice.setAccountName(invoiceFromDTO.getAccountName());
            invoice.setAccountNumber(invoiceFromDTO.getAccountNumber());
            invoice.setAdditionalDocument(invoiceFromDTO.getAdditionalDocument());
            invoice.setBankName(invoiceFromDTO.getBankName());
            invoice.setProductDocument(invoiceFromDTO.getProductDocument());
            invoice.setTotalDiscount(invoiceFromDTO.getTotalDiscount());
            invoice.setListProduct(invoiceFromDTO.getListProduct());
            invoiceDb.save(invoice);
        }
        return invoice;
    }
}


    


