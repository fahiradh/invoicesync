package com.megapro.invoicesync.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

   @Query("SELECT EXTRACT(MONTH FROM i.paymentDate) as month, SUM(i.grandTotal - i.taxTotal) as netRevenue " +
      "FROM Invoice i " +
      "WHERE i.status = 'Paid' " +
      "GROUP BY EXTRACT(MONTH FROM i.paymentDate) " +
      "ORDER BY month")
   List<Object[]> findMonthlyRevenue();

   @Query("SELECT CASE WHEN i.status = 'Approved' THEN 'Unpaid' ELSE i.status END AS status, COUNT(i) " +
      "FROM Invoice i " +
      "WHERE i.status IN ('Paid', 'Approved') " +
      "GROUP BY CASE WHEN i.status = 'Approved' THEN 'Unpaid' ELSE i.status END")
   List<Object[]> findInvoiceCountsByStatus();

    @Query("SELECT EXTRACT(YEAR FROM i.approvedDate) AS year, EXTRACT(MONTH FROM i.approvedDate) AS month, COUNT(i) AS invoiceCount " +
       "FROM Invoice i " +
       "WHERE i.status IN ('Approved', 'Paid') " +
       "AND i.staffEmail <> 'dummy' " + // Exclude invoices with 'dummy' email
       "GROUP BY EXTRACT(YEAR FROM i.approvedDate), EXTRACT(MONTH FROM i.approvedDate) " +
       "ORDER BY year, month")
    List<Object[]> findMonthlyInvoiceOutbound();

   @Query("SELECT SUM(i.grandTotal) FROM Invoice i WHERE i.status = 'Paid'")
   BigDecimal findTotalPaidAmount();

   @Query("SELECT SUM(i.grandTotal) FROM Invoice i WHERE i.status = 'Approved'")
   BigDecimal findTotalUnpaidAmount();

   @Query("SELECT SUM(i.grandTotal) FROM Invoice i WHERE i.dueDate < :today AND i.status = 'Unpaid'")
   BigDecimal findTotalOverdueAmount(@Param("today") LocalDate today);

   @Query("SELECT EXTRACT(MONTH FROM i.invoiceDate) AS month, COUNT(*) " +
         "FROM Invoice i " +
         "WHERE i.staffEmail <> 'dummy'" +
         "GROUP BY EXTRACT(MONTH FROM i.invoiceDate) " +
         "ORDER BY month")
   List<Object[]> findMonthlyInvoiceCounts();

   @Query(value = "SELECT EXTRACT(MONTH FROM i.invoice_date) AS month, i.status, COUNT(i) " +
   "FROM Invoice i " +
   "WHERE i.status IN ('Paid', 'Approved') " +
   "GROUP BY EXTRACT(MONTH FROM i.invoice_date), i.status " +
   "ORDER BY month", nativeQuery = true)
   List<Object[]> findMonthlyInvoiceStatusCounts();


   @Query("SELECT i.status AS status, COUNT(i) AS count " +
      "FROM Invoice i " +
      "WHERE i.staffEmail <> 'dummy' " + // Exclude records with 'dummy' email
      "GROUP BY i.status " +
      "ORDER BY i.status")
   List<Object[]> findInvoicesByStatus();

   @Query("SELECT i.status AS status, " +
       "SUM(CASE WHEN i.status = 'Paid' THEN 1 ELSE 0 END) AS paidCount, " +
       "SUM(CASE WHEN i.status = 'Approved' THEN 1 ELSE 0 END) AS unpaidCount " +
       "FROM Invoice i " +
       "WHERE i.status IN ('Paid', 'Approved') " +
       "GROUP BY i.status " +
       "ORDER BY i.status")
   List<Object[]> findInvoiceCountsByPaidAndApproved();

   @Query("SELECT EXTRACT(MONTH FROM i.paymentDate) AS month, SUM(i.taxTotal) AS totalTax " +
      "FROM Invoice i " +
      "WHERE i.status = 'Paid' " +
      "GROUP BY EXTRACT(MONTH FROM i.paymentDate) " +
      "ORDER BY month")
   List<Object[]> findTotalTaxByMonth();

   @Query("SELECT i FROM Invoice i WHERE i.staffEmail <> 'dummy' ORDER BY i.invoiceDate DESC")
   List<Invoice> findTopFiveNewestInvoices();

   @Query("SELECT i FROM Invoice i " +
      "WHERE i.status <> 'Paid' " + // Exclude invoices with status 'Paid'
      "ORDER BY ABS(i.dueDate - :today) ASC") // Order by closest due date
   List<Invoice> findTopFiveClosestDueDate(@Param("today") LocalDate today);

   @Query("SELECT i FROM Invoice i WHERE i.staffEmail = ?1 AND i.status = 'Approved' ORDER BY i.approvedDate DESC")
   List<Invoice> findTop5ApprovedInvoicesByStaffEmail(String staffEmail);

   @Query("SELECT i FROM Invoice i WHERE i.staffEmail = ?1 AND i.status = 'Need Revision' ")
   List<Invoice> findTop5NeedRevisionInvoicesByStaffEmail(String staffEmail);

   @Query("SELECT i FROM Invoice i " +
           "WHERE i.staffEmail = :staffEmail " + 
           "AND i.status <> 'Paid' " + 
           "ORDER BY ABS(i.dueDate - :today) ASC") 
   List<Invoice> findTopFiveClosestDueDateByStaffEmail(@Param("today") LocalDate today, @Param("staffEmail") String staffEmail);
}  

