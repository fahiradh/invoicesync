package com.megapro.invoicesync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.repository.ProductDb;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
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
    public List<Product> getAllProductDummyInvoice(Invoice dummy) {
        List<Product> listProduct = getAllProduct();
        System.out.println("lp servicec" + listProduct.size());
        List<Product> listDummy = new ArrayList<>();
        for (Product p : listProduct){
            if (p.getInvoice().getInvoiceId().equals(dummy.getInvoiceId())){
                listDummy.add(p);
            }
        }
        return listDummy;
    }
}
