package com.megapro.invoicesync.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.model.UserApp;
import com.megapro.invoicesync.repository.InvoiceDb;
import com.megapro.invoicesync.repository.ProductDb;
import com.megapro.invoicesync.repository.UserAppDb;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.ArrayList;

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
        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().length() == 0){
            var invoiceDate = invoice.getInvoiceDate();
            int month = invoiceDate.getMonthValue();
            String monthInRoman = convertToRoman(month);
            // DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
            String year = String.valueOf(invoiceDate.getYear());
            // String formattedYear = invoiceDate.format(year);
            String invoiceNumber = String.format("INV-%s/Krida/%s/%s", countStr,monthInRoman,year);
            invoice.setInvoiceNumber(invoiceNumber);
        }
        var dummy = getDummyInvoice();
        List<Product> newListProduct = new ArrayList<>();
        List<Product> listProduct = productService.getAllProductDummyInvoice(dummy);
        for (Product p : listProduct) {
            p.setInvoice(invoice);
            newListProduct.add(p);
        }
        invoice.setListProduct(newListProduct);
        calculateSubtotal(invoice);
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


    // private List<Product> getInvoiceListProduct(Invoice dummy){
    //     List<Product> listProductDummyInvoice = productService.getAllProductDummyInvoice(dummy);
    //     if (listProductDummyInvoice == null){
    //         return null;
    //     } else{
    //         return listProductDummyInvoice;
    //     }
    // }

    private void calculateSubtotal(Invoice invoice){
        double subtotal = 0;
        for (Product p : invoice.getListProduct()){
            subtotal += p.getTotalPrice().doubleValue();
        }
        invoice.setSubtotal(BigDecimal.valueOf(subtotal));
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

}


    


