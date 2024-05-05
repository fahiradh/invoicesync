package com.megapro.invoicesync.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megapro.invoicesync.model.Approval;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.repository.CustomerDb;
import com.megapro.invoicesync.repository.InvoiceDb;
import com.megapro.invoicesync.repository.ProductDb;

@Service
public class DashboardServiceImpl implements DashboardService {
    
    @Autowired
    InvoiceDb invoiceDb;

    @Autowired
    CustomerDb customerDb;

    @Autowired
    ProductDb productDb;
    
    @Override
    public List<Object[]> getMonthlyRevenue() {
        return invoiceDb.findMonthlyRevenue();
    }

    @Override
    public List<Object[]> getInvoiceCountsByStatus() {
        return invoiceDb.findInvoiceCountsByStatus();
    }

    @Override
    public List<Object[]> getTopCustomersByInvoiceCount() {
        return customerDb.findTopCustomersByInvoiceCount();
    }

    @Override
    public List<Object[]> getTopProductsByQuantityOrdered() {
        return productDb.findTopProductsByQuantityOrdered();
    }

    @Override
    public BigDecimal getInvoicePaidAmount() {
        return invoiceDb.findTotalPaidAmount();
    }

    @Override
    public BigDecimal getInvoiceUnpaidAmount() {
        return invoiceDb.findTotalUnpaidAmount();
    }

    @Override
    public BigDecimal getInvoiceOverdueAmount() {
        return invoiceDb.findTotalOverdueAmount(LocalDate.now());
    }

    @Override
    public List<Object[]> getOutboundInvoicePerMonth(){
        return invoiceDb.findMonthlyInvoiceOutbound();
    }

    @Override
    public int totalInvoiceApproved(String email) {
        List<Invoice> allInvoices = invoiceDb.findAll();
        int count = 0;

        for (Invoice inv : allInvoices) {
            // Check if the invoice is "Approved"
            if (inv.getStatus() != null && inv.getStatus().equals("Approved")) {
                // Loop through the list of approvals
                for (Approval approval : inv.getListApproval()) {
                    // Check if the approver's email matches the given email
                    if (approval.getEmployee().getEmail().equals(email)) {
                        count++; // Increment the count
                        break; // No need to check further for this invoice
                    }
                }
            }
        }

        return count; // Return the total count
    }

    @Override
    public int totalInvoiceWaitingApproved(String email) {
        List<Invoice> allInvoices = invoiceDb.findAll();
        int count = 0;

        for (Invoice inv : allInvoices) {
            // Check if the invoice is "Approved"
            if (inv.getStatus() != null && inv.getStatus().equals("Need Approval")) {
                // Loop through the list of approvals
                for (Approval approval : inv.getListApproval()) {
                    // Check if the approver's email matches the given email
                    if (approval.getEmployee().getEmail().equals(email)) {
                        count++; // Increment the count
                        break; // No need to check further for this invoice
                    }
                }
            }
        }

        return count; // Return the total count
    }

    @Override 
    public List<Object[]> getInvoiceStatusCounts(){
        return invoiceDb.findInvoicesByStatus();
    }

    @Override
    public List<Object[]> getInvoiceCountsByPaidAndApproved() {
        return invoiceDb.findInvoiceCountsByPaidAndApproved();
    }

    @Override
    public List<Object[]> getMonthlyTaxGainFromPaidInvoices(){
        List<Object[]> rawResults = invoiceDb.findTotalTaxByMonth();
        Map<Integer, Double> monthToTax = new HashMap<>();

        // Fill the map with query results
        for (Object[] result : rawResults) {
            int month = (int) result[0]; // Extracted month
            double totalTax = ((Number) result[1]).doubleValue(); // Total tax

            monthToTax.put(month, totalTax);
        }

        // Complete list of months with zero as default value
        List<Object[]> fullResults = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            fullResults.add(new Object[]{
                i, // Month (as integer)
                monthToTax.getOrDefault(i, 0.0) // Default to zero if no data
            });
        }

        return fullResults;
    }

    @Override
    public List<Invoice> getFiveNewestInvoices() {
        List<Invoice> invoices = invoiceDb.findTopFiveNewestInvoices();
        return invoices.size() > 5 ? invoices.subList(0, 5) : invoices;
    }
    
    @Override
    public List<Invoice> getFiveDueClosestInvoices(){
        LocalDate today = LocalDate.now(); // Get today's date
        List<Invoice> invoices = invoiceDb.findTopFiveClosestDueDate(today); // Fetch all ordered by closest due date
        return invoices.subList(0, Math.min(5, invoices.size()));
    }
}
