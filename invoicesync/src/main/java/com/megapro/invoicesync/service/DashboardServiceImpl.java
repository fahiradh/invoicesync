package com.megapro.invoicesync.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable("invoiceStatusCounts")
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
}
