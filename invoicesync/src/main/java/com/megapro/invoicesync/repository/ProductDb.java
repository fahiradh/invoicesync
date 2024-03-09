package com.megapro.invoicesync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface ProductDb extends JpaRepository<Product, UUID>{
    List<Product> findByInvoice(Invoice invoice);
}
