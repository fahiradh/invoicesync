package com.megapro.invoicesync.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megapro.invoicesync.dto.request.CreateInvoiceRequestDTO;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.model.Tax;
import com.megapro.invoicesync.model.UserApp;
import com.megapro.invoicesync.repository.InvoiceDb;
import com.megapro.invoicesync.repository.ProductDb;
import com.megapro.invoicesync.repository.UserAppDb;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.ArrayList;
import java.time.LocalDate;

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
    UserAppDb userAppDb;

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
    public void attributeInvoice(Invoice invoice) {
        long count = countInvoice()+1;
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
        // parseListTax(invoice);
        calculateSubtotal(invoice);
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
        // System.out.println("list product "+invoice.getListProduct());
        for (Product p : invoice.getListProduct()){
            System.out.println("produk "+ p);
            total += p.getTotalPrice().doubleValue();
        }
        double totalAftDisc = total - ((invoice.getTotalDiscount()/100.0) * total);
        // double subtotal = 0;
        // for (Tax tax : invoice.getListTax()){
        //     subtotal = totalAftDisc * (tax.getTaxPercentage()/100.0);
        // }
        invoice.setSubtotal(BigDecimal.valueOf(totalAftDisc));
    }

    // private void parseListTax(Invoice invoice){
    //     var dummy = getDummyInvoice();
    //     List<Tax> dummyListTax = dummy.getListTax();
    //     List<Tax> invoiceListTax = new ArrayList<>();
    //     for (Tax tax : dummyListTax){
    //         invoiceListTax.add(tax);
    //     }
    //     invoice.setListTax(invoiceListTax);
    //     dummy.setListTax(new ArrayList<>());
    // }

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
    public void transferData(CreateInvoiceRequestDTO invoiceRequestDTO, Invoice invoice){
        invoiceRequestDTO.setAccountName(invoice.getAccountName());
        invoiceRequestDTO.setAccountNumber(invoice.getAccountNumber());
        invoiceRequestDTO.setAdditionalDocument(invoice.getAdditionalDocument());
        invoiceRequestDTO.setBankName(invoice.getBankName());
        invoiceRequestDTO.setCity(invoice.getCity());
        invoiceRequestDTO.setCustomerId(invoice.getCustomer().getCustomerId());
        invoiceRequestDTO.setDueDate(invoice.getDueDate());
        invoiceRequestDTO.setStatus(invoice.getStatus());
        invoiceRequestDTO.setSignature(invoice.getSignature());
        invoiceRequestDTO.setStaffEmail(invoice.getStaffEmail());
        invoiceRequestDTO.setSubtotal(invoice.getSubtotal());
        invoiceRequestDTO.setTotalWords(invoice.getTotalWords());
        invoiceRequestDTO.setTotalDiscount(invoice.getTotalDiscount());
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
}


    


