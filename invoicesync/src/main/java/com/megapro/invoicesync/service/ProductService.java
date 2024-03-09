package com.megapro.invoicesync.service;

import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import java.util.List;

public interface ProductService {
    void createProduct(Product product);
    List<Product> getAllProduct();
    List<Product> getAllProductDummyInvoice(Invoice dummy);
}
