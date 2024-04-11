package com.megapro.invoicesync.restcontroller;

import org.springframework.web.bind.annotation.RestController;

import com.megapro.invoicesync.dto.ProductMapper;
import com.megapro.invoicesync.dto.request.CreateProductRequestDTO;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.service.InvoiceService;
import com.megapro.invoicesync.service.ProductService;
import com.megapro.invoicesync.service.TaxService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/v1")
public class ProductRestController {
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
        System.out.println("product price" + productDTO.getPrice());
        System.out.println("product total price" + productDTO.getTotalPrice());

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

    @PostMapping("/delete-product")
    public ResponseEntity<Product> deleteProduct(@RequestBody CreateProductRequestDTO productDTO){
        var product = productService.getProduct(productDTO);
        productService.delete(product);
        System.out.println("DELETE DONE");
        return ResponseEntity.ok(product);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        var listProduct = productService.getAllProduct();
        return ResponseEntity.ok(listProduct);
    }
    
}
