package com.megapro.invoicesync.controller;

import java.math.BigDecimal;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.time.format.TextStyle;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.megapro.invoicesync.dto.response.InvoiceStatusCountDTO;
import com.megapro.invoicesync.dto.response.TopCustomerDTO;
import com.megapro.invoicesync.dto.response.TopProductDTO;
import com.megapro.invoicesync.service.DashboardService;
import com.megapro.invoicesync.util.classes.Revenue;


@RestController
public class DashboardController {

    @Autowired
    DashboardService dashboardService;

    // Dashboard direktur //

    @GetMapping("/api/dashboard/revenue")
    @ResponseBody
    public ResponseEntity<List<Revenue>> getMonthlyRevenue() {
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
        return new ResponseEntity<>(revenues, HttpStatus.OK);
    }
    
    @GetMapping("api/dashboard/top-customers")
    @ResponseBody
    public ResponseEntity<List<TopCustomerDTO>> showTopCustomers(Model model) {
        List<Object[]> topCustomersData = dashboardService.getTopCustomersByInvoiceCount();
        List<TopCustomerDTO> topCustomers = new ArrayList<>();

        for (Object[] data : topCustomersData) {
            String customerName = (String) data[0];
            Long invoiceCount = (Long) data[1];
            topCustomers.add(new TopCustomerDTO(customerName, invoiceCount));
        }
        return new ResponseEntity<>(topCustomers, HttpStatus.OK);
    }

    @GetMapping("api/dashboard/top-products")
    @ResponseBody
    public ResponseEntity<List<TopProductDTO>> showTopProducts(Model model) {
        List<Object[]> topProductsData = dashboardService.getTopProductsByQuantityOrdered();
        List<TopProductDTO> topProducts = new ArrayList<>();

        for (Object[] data : topProductsData) {
            String productName = (String) data[0];
            BigDecimal invoiceCount = (BigDecimal) data[1];
            topProducts.add(new TopProductDTO(productName, invoiceCount));
        }
        return new ResponseEntity<>(topProducts, HttpStatus.OK);
    }
    
    @GetMapping("api/dashboard/invoice-ratio")
    @ResponseBody
    public ResponseEntity<List<InvoiceStatusCountDTO>> showInvoiceRatio(Model model) {
        List<Object[]> invoiceStatusCountsData = dashboardService.getInvoiceCountsByStatus();
        List<InvoiceStatusCountDTO> invoiceStatusCounts = new ArrayList<>();

        for (Object[] data : invoiceStatusCountsData) {
            String status = (String) data[0];
            Long invoiceCount = (Long) data[1];
            invoiceStatusCounts.add(new InvoiceStatusCountDTO(status, invoiceCount));
        }
        return new ResponseEntity<>(invoiceStatusCounts, HttpStatus.OK);
    }
    
}
