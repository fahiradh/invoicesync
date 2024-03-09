package com.megapro.invoicesync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megapro.invoicesync.model.Product;

import java.time.LocalDateTime;
import jakarta.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ProductDb extends JpaRepository<Product, Long>{
    List<Product> findByCreatedBefore(LocalDateTime time);
}
