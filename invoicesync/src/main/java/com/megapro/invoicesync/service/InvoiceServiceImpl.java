package com.megapro.invoicesync.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megapro.invoicesync.dto.response.ReadInvoiceResponse;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.model.UserApp;
import com.megapro.invoicesync.repository.InvoiceDb;
import com.megapro.invoicesync.repository.ProductDb;
import com.megapro.invoicesync.repository.UserAppDb;

import jakarta.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import java.util.List;
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
        var count = countInvoice()+1;
        
        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().length() == 0){
            var invoiceDate = invoice.getInvoiceDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");
            String formattedDate = invoiceDate.format(formatter);
            String invoiceNumber = String.format("INV-%s/KRIDA/%s", count,formattedDate);
            invoice.setInvoiceNumber(invoiceNumber);
        }
        List<Product> listProduct = productService.getAllProduct();
        List<Product> newListProduct = new ArrayList<>();
        for (Product p : listProduct) {
            if (p.getCreated().isEqual(invoice.getCreated())) {
                p.setInvoice(invoice);
                productDb.save(p);
                newListProduct.add(p);
            }
        }
        invoice.setListProduct(newListProduct);
        invoiceDb.save(invoice);
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
    public List<Invoice> retrieveInvoicesByEmailAndStatus(String email, String status) {
        return invoiceDb.findByStaffEmailAndStatus(email, status);
    }
    
    
}


    


