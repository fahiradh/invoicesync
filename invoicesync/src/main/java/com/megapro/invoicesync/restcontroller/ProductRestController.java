package com.megapro.invoicesync.restcontroller;

import org.springframework.web.bind.annotation.RestController;

import com.megapro.invoicesync.dto.ProductMapper;
import com.megapro.invoicesync.dto.request.CreateProductRequestDTO;
import com.megapro.invoicesync.dto.request.UpdateProductRequestDTO;
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
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        var dummyInvoice = invoiceService.getDummyInvoice();
        var product = productMapper.createProductRequestToProduct(productDTO);
        product.setInvoice(dummyInvoice);
        double totalPrice = Double.parseDouble(productDTO.getTotalPrice());
        BigDecimal fixedPrice = BigDecimal.valueOf(totalPrice);
        product.setTotalPrice(fixedPrice);
        productService.createProduct(product);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/product/{id}/delete")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") String productId){
        var product = productService.getProductById(UUID.fromString(productId));
        productService.delete(product);
        System.out.println("delete done");
        return ResponseEntity.ok(product);
    }

    @PostMapping("/update-update")
    public ResponseEntity<Product> updateProduct(@RequestBody UpdateProductRequestDTO productDTO){
        var productFromDTO = productMapper.updateProductRequestToProduct(productDTO);
        var product = productService.updateProduct(productFromDTO);
        return ResponseEntity.ok(product);
    }
}
