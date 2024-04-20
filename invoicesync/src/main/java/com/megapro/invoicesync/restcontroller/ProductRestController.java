package com.megapro.invoicesync.restcontroller;

import org.springframework.web.bind.annotation.RestController;

import com.megapro.invoicesync.dto.ProductMapper;
import com.megapro.invoicesync.dto.request.CreateProductRequestDTO;
import com.megapro.invoicesync.dto.request.UpdateProductRequestDTO;
import com.megapro.invoicesync.dto.response.ReadProductResponseDTO;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.service.ExcelService;
import com.megapro.invoicesync.service.InvoiceService;
import com.megapro.invoicesync.service.ProductService;
import com.megapro.invoicesync.service.TaxService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collections;

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

    @Autowired
    ExcelService excelService;

    @PostMapping("/create-product")
    public ResponseEntity<ReadProductResponseDTO> createProduct(@RequestBody CreateProductRequestDTO productDTO) {
        var dummyInvoice = invoiceService.getDummyInvoice();
        var product = productMapper.createProductRequestToProduct(productDTO);
        product.setInvoice(dummyInvoice);
        double totalPrice = Double.parseDouble(productDTO.getTotalPrice());
        BigDecimal fixedPrice = BigDecimal.valueOf(totalPrice);
        product.setTotalPrice(fixedPrice);
        productService.createProduct(product);
        var readProductDTO = productMapper.readProductToProductDTO(product);
        return ResponseEntity.ok(readProductDTO);
    }

    @PostMapping("/create-product-document")
    public ResponseEntity<List<ReadProductResponseDTO>> createProductByDocument(@RequestParam("productDocument") MultipartFile productDocument) throws IOException{
        if (productDocument != null && !productDocument.isEmpty()) {
            List<Product> listProduct = excelService.processExcel(productDocument);
            List<ReadProductResponseDTO> listProductDTO = new ArrayList<>();
            for (Product p : listProduct) {
                var productDTO = productMapper.readProductToProductDTO(p);
                listProductDTO.add(productDTO);
            }
            return ResponseEntity.ok(listProductDTO);
        } else {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @PostMapping("/create-product/{invoiceId}")
    public ResponseEntity<ReadProductResponseDTO> updateProductInvoice(@RequestBody CreateProductRequestDTO productDTO,
                                                            @PathVariable("invoiceId") String invoiceId) {
        var invoice = invoiceService.getInvoiceById(UUID.fromString(invoiceId));
        var product = productMapper.createProductRequestToProduct(productDTO);
        product.setInvoice(invoice);
        double totalPrice = Double.parseDouble(productDTO.getTotalPrice());
        BigDecimal fixedPrice = BigDecimal.valueOf(totalPrice);
        product.setTotalPrice(fixedPrice);
        productService.createProduct(product);
        var readProductDTO = productMapper.readProductToProductDTO(product);
        return ResponseEntity.ok(readProductDTO);
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

    @GetMapping("/invoice/product/{id}")
    public ResponseEntity<List<ReadProductResponseDTO>> getAllProduct(@PathVariable("id") String productId){
        System.out.println("Masuk ke rest controller product");
        List<Product> listProduct = productService.getProductByInvoice(productId);
        List<ReadProductResponseDTO> listProductDTO = new ArrayList<>();
        for (Product p : listProduct){
            var productDTO = productMapper.readProductToProductDTO(p);
            listProductDTO.add(productDTO);
        }
        return ResponseEntity.ok(listProductDTO);
    }
}
