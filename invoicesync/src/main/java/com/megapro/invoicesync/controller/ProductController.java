package com.megapro.invoicesync.controller;

import org.springframework.web.bind.annotation.RestController;

import com.megapro.invoicesync.dto.ProductMapper;
import com.megapro.invoicesync.dto.request.CreateProductRequestDTO;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.service.InvoiceService;
import com.megapro.invoicesync.service.ProductService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class ProductController {
    @Autowired
    ProductService productService;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    InvoiceService invoiceService;

    @PostMapping("/create-product")
    public ResponseEntity<String> createProduct(@RequestBody CreateProductRequestDTO productDTO) {
        var product = productMapper.createProductRequestToProduct(productDTO);
        var dummyInvoice = invoiceService.getInvoiceByStaffEmail("dummy");
        System.out.println("dummy invoice "+ dummyInvoice.size());
        for (Invoice inv : dummyInvoice){
            product.setInvoice(inv);
        }
        var totalPrice = productDTO.getTotalPrice().replace(".", "");
        long number = Long.parseLong(totalPrice);
        product.setTotalPrice(number/100);
        productService.createProduct(product);
        return ResponseEntity.ok("Ok");
    }
    
}
