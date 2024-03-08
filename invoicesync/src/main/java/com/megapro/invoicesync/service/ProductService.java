package com.megapro.invoicesync.service;

import com.megapro.invoicesync.dto.request.CreateProductRequestDTO;
import com.megapro.invoicesync.model.Product;
import java.util.List;
import java.time.LocalDateTime;

public interface ProductService {
    void createProduct(Product product);
    List<Product> getAllProduct();
    List<Product> findByCreatedBefore(LocalDateTime time);
}
