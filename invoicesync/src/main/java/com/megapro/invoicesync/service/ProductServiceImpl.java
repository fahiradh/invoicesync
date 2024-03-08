package com.megapro.invoicesync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.repository.ProductDb;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService{
    @Autowired
    ProductDb productDb;

    @Override
    public void createProduct(Product product) {
        productDb.save(product);
    }
    
    @Override
    public List<Product> getAllProduct(){
        return productDb.findAll();
    }

    @Override
    public List<Product> findByCreatedBefore(LocalDateTime time) {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        List<Product> products = productDb.findByCreatedBefore(oneMinuteAgo);
        return products;
    }
}
