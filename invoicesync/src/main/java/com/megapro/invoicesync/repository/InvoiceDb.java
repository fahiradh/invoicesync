package com.megapro.invoicesync.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megapro.invoicesync.model.Invoice;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface InvoiceDb extends JpaRepository<Invoice, UUID>{
    Invoice getInvoiceByInvoiceNumber(String id);
    List<Invoice> findByStaffEmailIn(List<String> emails);
    List<Invoice> findByStaffEmail(String email);
    List<Invoice> findByStatus(String status);
    List<Invoice> findByStaffEmailAndStatus(String email, String status);
}

