package com.megapro.invoicesync.service;

import java.util.List;

public interface DashboardService {
    List<Object[]> getMonthlyRevenue();
    List<Object[]> getInvoiceCountsByStatus();
    List<Object[]> getTopCustomersByInvoiceCount();
    List<Object[]> getTopProductsByQuantityOrdered();
}
