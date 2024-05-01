package com.megapro.invoicesync.controller;

import java.math.BigDecimal;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.time.format.TextStyle;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.megapro.invoicesync.service.DashboardService;
import com.megapro.invoicesync.util.classes.Revenue;


@Controller
public class DashboardController {

    @Autowired
    DashboardService dashboardService;

    // Dashboard direktur //

    @GetMapping("/revenue")
    public String showRevenueChart(Model model) {
        List<Object[]> revenueData = dashboardService.getMonthlyRevenue();
        BigDecimal[] monthlyRevenue = new BigDecimal[12]; 
        Arrays.fill(monthlyRevenue, BigDecimal.ZERO); 

        for (Object[] data : revenueData) {
            int monthIndex = (int) data[0] - 1;
            monthlyRevenue[monthIndex] = (BigDecimal) data[1];
        }

        List<Revenue> revenues = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            String month = Month.of(i + 1).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            revenues.add(new Revenue(month, monthlyRevenue[i]));
        }
        model.addAttribute("revenues", revenues);
        return "dashboard/finance-director/revenue.html";
    }

    @GetMapping("/top-customers")
    public String showTopCustomers(Model model) {
        List<Object[]> topCustomers = dashboardService.getTopCustomersByInvoiceCount();
        model.addAttribute("topCustomers", topCustomers);
        return "dashboard/finance-director/top-customers.html";
    }

    @GetMapping("/top-products")
    public String showTopProducts(Model model) {
        List<Object[]> topProducts = dashboardService.getTopProductsByQuantityOrdered();
        model.addAttribute("topProducts", topProducts);
        return "dashboard/finance-director/top-products.html";
    }

    @GetMapping("/invoice-ratio")
    public String showInvoiceRatio(Model model) {
        List<Object[]> invoiceStatusCounts = dashboardService.getInvoiceCountsByStatus();
        model.addAttribute("invoiceStatusCounts", invoiceStatusCounts);
        return "dashboard/finance-director/invoice-status-ratio.html";
    }
    


    
    
}
