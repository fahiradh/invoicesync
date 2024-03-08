package com.megapro.invoicesync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.repository.InvoiceDb;
import com.megapro.invoicesync.repository.ProductDb;

import jakarta.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import java.util.List;
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

    @Override
    public void createInvoice(Invoice invoice) {
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
}
