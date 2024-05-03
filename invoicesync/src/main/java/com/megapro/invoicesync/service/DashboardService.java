package com.megapro.invoicesync.service;

import java.util.List;
import java.math.BigDecimal;
public interface DashboardService {
    List<Object[]> getMonthlyRevenue();
    List<Object[]> getInvoiceCountsByStatus();
    List<Object[]> getTopCustomersByInvoiceCount();
    List<Object[]> getTopProductsByQuantityOrdered();
    BigDecimal getInvoicePaidAmount();
    BigDecimal getInvoiceUnpaidAmount();
    BigDecimal getInvoiceOverdueAmount();
}
