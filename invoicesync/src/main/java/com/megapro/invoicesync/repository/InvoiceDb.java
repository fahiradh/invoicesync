package com.megapro.invoicesync.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.megapro.invoicesync.model.Invoice;
import jakarta.transaction.Transactional;


@Repository
@Transactional
public interface InvoiceDb extends JpaRepository<Invoice, UUID>{
    Invoice getInvoiceByInvoiceNumber(String id);
    @Query("SELECT i FROM Invoice i WHERE i.staffEmail LIKE 'dummy'")
    Invoice findDummyInvoice();
    List<Invoice> findByStaffEmailIn(List<String> emails);
    List<Invoice> findByStaffEmail(String email);
    List<Invoice> findByStatus(String status);
    List<Invoice> findByStaffEmailAndStatus(String email, String status);
    @Query("SELECT i FROM Invoice i JOIN Employee e ON i.staffEmail = e.email JOIN Role r ON e.role = r WHERE r.role LIKE %:roleName% AND i.status = :status")
    List<Invoice> findByEmployeeRoleNameAndStatus(String roleName, String status);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}

