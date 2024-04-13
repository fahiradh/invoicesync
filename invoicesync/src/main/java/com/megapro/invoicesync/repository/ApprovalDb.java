package com.megapro.invoicesync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megapro.invoicesync.model.Approval;
import com.megapro.invoicesync.model.Employee;
import com.megapro.invoicesync.model.Invoice;

import jakarta.transaction.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ApprovalDb extends JpaRepository<Approval, Integer>{
    List<Approval> findByInvoice(Invoice invoice);
    boolean existsByInvoiceAndEmployee(Invoice invoice, Employee employee);
    Approval findByApprovalId(int approvalId);
    // List<Invoice> findByApproval_id(int approval_id);
}
