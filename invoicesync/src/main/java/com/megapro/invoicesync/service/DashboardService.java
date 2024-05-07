package com.megapro.invoicesync.service;

import java.util.List;

import com.megapro.invoicesync.model.Invoice;

import java.math.BigDecimal;
public interface DashboardService {
    List<Object[]> getMonthlyRevenue();
    List<Object[]> getInvoiceCountsByStatus();
    List<Object[]> getTopCustomersByInvoiceCount();
    List<Object[]> getTopProductsByQuantityOrdered();
    List<Object[]> getOutboundInvoicePerMonth();
    int totalInvoiceApproved(String email);
    int totalInvoiceWaitingApproved(String email);
    List<Object[]> getInvoiceStatusCounts();
    List<Object[]> getInvoiceCountsByPaidAndApproved();
    List<Object[]> getMonthlyTaxGainFromPaidInvoices();
    List<Invoice> getFiveNewestInvoices();
    List<Invoice> getFiveDueClosestInvoices();
    BigDecimal getInvoicePaidAmount();
    BigDecimal getInvoiceUnpaidAmount();
    BigDecimal getInvoiceOverdueAmount();
    List<Object[]> getMonthlyInvoiceCounts();
    List<Object[]> getMonthlyInvoiceStatusCounts();
    List<Invoice> getTop5ApprovedInvoicesByStaffEmail(String staffEmail);
    List<Invoice> getTop5NeedRevisionInvoicesByStaffEmail(String staffEmail);
    List<Invoice> getFiveDueClosestInvoicesByStaffEmail(String staffEmail);
}
