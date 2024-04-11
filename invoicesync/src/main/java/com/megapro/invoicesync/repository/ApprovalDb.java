package com.megapro.invoicesync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.megapro.invoicesync.model.Approval;
import com.megapro.invoicesync.model.Employee;
import com.megapro.invoicesync.model.Invoice;
import java.util.List;
public interface ApprovalDb extends JpaRepository<Approval, Integer>{
    List<Approval> findByInvoice(Invoice invoice);
    boolean existsByInvoiceAndEmployee(Invoice invoice, Employee employee);
}
