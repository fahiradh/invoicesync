package com.megapro.invoicesync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.megapro.invoicesync.model.Tax;

public interface TaxDb extends JpaRepository<Tax,Integer>{
    Tax findByTaxId(int taxId);
    
}
