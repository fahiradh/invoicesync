package com.megapro.invoicesync.controller;

import org.springframework.web.bind.annotation.RestController;

import com.megapro.invoicesync.dto.ProductMapper;
import com.megapro.invoicesync.dto.request.CreateInvoiceRequestDTO;
import com.megapro.invoicesync.dto.request.CreateProductRequestDTO;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.model.Tax;
import com.megapro.invoicesync.service.InvoiceService;
import com.megapro.invoicesync.service.ProductService;
import com.megapro.invoicesync.service.TaxService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ProductController {
    @Autowired
    ProductService productService;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    InvoiceService invoiceService;
    
    @Autowired
    TaxService taxService;

    @PostMapping("/create-product")
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequestDTO productDTO) {
        System.out.println("MASUK KE SINI SUCCESS");
        System.out.println(productDTO.getDescription());
        System.out.println(productDTO.getName());
        System.out.println(productDTO.getPrice());
        var dummyInvoice = invoiceService.getDummyInvoice();
        var product = productMapper.createProductRequestToProduct(productDTO);
        product.setInvoice(dummyInvoice);
        System.out.println("product created");
        double totalPrice = Double.parseDouble(productDTO.getTotalPrice());
        BigDecimal fixedPrice = BigDecimal.valueOf(totalPrice);
        product.setTotalPrice(fixedPrice);
        productService.createProduct(product);
        return ResponseEntity.ok(product);
    }
}
