package com.megapro.invoicesync.service;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
