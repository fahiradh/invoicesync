package com.megapro.invoicesync.util.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.megapro.invoicesync.service.DashboardServiceImpl;

@Component
public class CacheRefreshScheduler {
    @Autowired
    private DashboardServiceImpl dashboardService;

    @Autowired
    private CacheManager cacheManager;

    @SuppressWarnings("null")
    @Scheduled(fixedRate = 3600000) // Every hour
    public void refreshCache() {
        cacheManager.getCache("invoiceCounts").clear(); // Clear specific cache
        dashboardService.getInvoiceCountsByStatus(); // Refresh data
    }
}
