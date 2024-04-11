package com.megapro.invoicesync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megapro.invoicesync.dto.request.CreateProductRequestDTO;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.repository.ProductDb;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

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
    public List<Product> getAllProductDummyInvoice(Invoice dummy) {
        List<Product> listProduct = getAllProduct();
        List<Product> listDummy = new ArrayList<>();
        for (Product p : listProduct){
            if (p.getInvoice().getInvoiceId().equals(dummy.getInvoiceId())){
                listDummy.add(p);
            }
        }
        return listDummy;
    }

    @Override
    public Product getProduct(CreateProductRequestDTO productDTO) {
        List<Product> listProduct = getAllProduct();
        double totalPrice = Double.parseDouble(productDTO.getTotalPrice());
        double price = Double.parseDouble(productDTO.getPrice());
        for (Product p : listProduct){
            if (p.getDescription().equals(productDTO.getDescription())){
                    return p;
                }
        }
        return null;
    }

    @Override
    public void delete(Product product) {
        productDb.delete(product);
    }
}
