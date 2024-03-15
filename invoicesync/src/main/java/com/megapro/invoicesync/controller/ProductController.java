package com.megapro.invoicesync.controller;

import org.springframework.web.bind.annotation.RestController;

import com.megapro.invoicesync.dto.ProductMapper;
import com.megapro.invoicesync.dto.request.CreateProductRequestDTO;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.service.InvoiceService;
import com.megapro.invoicesync.service.ProductService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

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
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequestDTO productDTO) {
        var invoiceId = invoiceService.getDummyInvoice();
        var product = productMapper.createProductRequestToProduct(productDTO);
        var invoice = invoiceService.getInvoiceById(invoiceId.getInvoiceId());
        product.setInvoice(invoice);
        double totalPrice = Double.parseDouble(productDTO.getTotalPrice());
        double calcDisc = totalPrice/100.0;
        BigDecimal fixedPrice = BigDecimal.valueOf(calcDisc);
        product.setTotalPrice(fixedPrice);
        productService.createProduct(product);
        return ResponseEntity.ok(product);
    }
    
}
