package com.megapro.invoicesync.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.model.UserApp;
import com.megapro.invoicesync.repository.InvoiceDb;
import com.megapro.invoicesync.repository.ProductDb;
import com.megapro.invoicesync.repository.UserAppDb;

import jakarta.transaction.Transactional;
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
    public void attributeInvoce(Invoice invoice) {
        long count = countInvoice() + 1;
        String countStr = String.format("%03d", count);
        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().length() == 0){
            var invoiceDate = invoice.getInvoiceDate();
            DateTimeFormatter dayMonth = DateTimeFormatter.ofPattern("ddMM");
            DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
            String formattedDate = invoiceDate.format(dayMonth);
            String formattedYear = invoiceDate.format(year);
            String invoiceNumber = String.format("INV/%s/KRD/%s/%s", formattedYear,formattedDate,countStr);
            invoice.setInvoiceNumber(invoiceNumber);
        }
        var listDummy = invoiceDb.findByStaffEmail("dummy");
        List<Product> newListProduct = new ArrayList<>();
        Invoice dummy = null;
        for (Invoice inv : listDummy){
            System.out.println("aman take in");
            System.out.println(inv.getInvoiceId());
            dummy = inv;
        }
        System.out.println("summy id "+dummy.getInvoiceId());

        List<Product> listProduct = productService.getAllProductDummyInvoice(dummy);
        System.out.println("prod length "+listProduct.size());
        for (Product p : listProduct) {
            System.out.println("AMAN");
            System.out.println(p.getProductId());
            p.setInvoice(invoice);
            newListProduct.add(p);
        }
        invoice.setListProduct(newListProduct);
        calculateSubtotal(invoice);
    }

    private void calculateSubtotal(Invoice invoice){
        long subtotal = 0;
        for (Product p : invoice.getListProduct()){
            subtotal += p.getTotalPrice();
        }
        invoice.setSubtotal(subtotal);
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
    
    
}


    


