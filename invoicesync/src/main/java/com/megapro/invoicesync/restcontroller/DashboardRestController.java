package com.megapro.invoicesync.restcontroller;

import java.math.BigDecimal;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.megapro.invoicesync.dto.response.InvoicePerMonthDTO;
import com.megapro.invoicesync.dto.response.InvoiceStatusCountDTO;
import com.megapro.invoicesync.dto.response.MonthStatusDTO;
import com.megapro.invoicesync.dto.response.TopCustomerDTO;
import com.megapro.invoicesync.dto.response.TopProductDTO;
import com.megapro.invoicesync.dto.response.InvoicesStatusChartDTO;
import com.megapro.invoicesync.dto.response.MonthlyTaxDTO;
import com.megapro.invoicesync.dto.response.NewestInvoiceDTO;
import com.megapro.invoicesync.dto.response.OutboundInvoiceCountDTO;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.service.DashboardService;
import com.megapro.invoicesync.util.classes.Revenue;



@RestController
public class DashboardRestController {

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


    // Dashboard Manager Non Finance //

    @GetMapping("/api/outbound-invoices") 
    @ResponseBody
    public ResponseEntity<List<OutboundInvoiceCountDTO>> showInvoiceOutboundAPI(Model model) {
        List<Object[]> rawData = dashboardService.getOutboundInvoicePerMonth();
        List<OutboundInvoiceCountDTO> response = new ArrayList<>();

        // Convert raw data to DTO
        final String[] monthNames = {
            "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
        };

        for (Object[] item : rawData) {
            int year = ((Number) item[0]).intValue();
            int month = ((Number) item[1]).intValue();
            int count = ((Number) item[2]).intValue();

            String monthName = monthNames[month - 1] + " " + year;
            response.add(new OutboundInvoiceCountDTO(monthName, count));
        }

        return new ResponseEntity<>(response, HttpStatus.OK); // Return the JSON data
    }

    @GetMapping("/api/invoice-status")
    @ResponseBody
    public ResponseEntity<List<InvoicesStatusChartDTO>> getInvoiceStatusData(Model model) {
        List<Object[]> rawData = dashboardService.getInvoiceStatusCounts();
        List<InvoicesStatusChartDTO> response = new ArrayList<>();

        // Convert raw data to DTOs
        for (Object[] item : rawData) {
            String status = (String) item[0]; // Status of the invoice
            int count = ((Number) item[1]).intValue(); // Count of invoices with this status

            response.add(new InvoicesStatusChartDTO(status, count));
        }

        return new ResponseEntity<>(response, HttpStatus.OK); // Return JSON data
    }

    @GetMapping("/api/invoice-status-bar")
    @ResponseBody
    public ResponseEntity<List<InvoicesStatusChartDTO>> getInvoiceStatusCounts() {
        List<Object[]> rawData = dashboardService.getInvoiceCountsByPaidAndApproved();
        List<InvoicesStatusChartDTO> response = new ArrayList<>();

        for (Object[] item : rawData) {
            String status = (String) item[0]; // Invoice status
            int count = ((Number) item[1]).intValue(); // Invoice count
            response.add(new InvoicesStatusChartDTO(status, count));
        }

        return new ResponseEntity<>(response, HttpStatus.OK); // Return the JSON response
    }

    @GetMapping("/api/tax-gain-chart")
    @ResponseBody
    public ResponseEntity<List<MonthlyTaxDTO>> getTaxGainData() {
        List<Object[]> rawData = dashboardService.getMonthlyTaxGainFromPaidInvoices();
        List<MonthlyTaxDTO> response = new ArrayList<>();

        // Convert raw data to DTOs
        for (Object[] item : rawData) {
            int month = ((Number) item[0]).intValue();
            double totalTax = ((Number) item[1]).doubleValue();
            response.add(new MonthlyTaxDTO(month, totalTax));
        }

        return new ResponseEntity<>(response, HttpStatus.OK); // Return JSON data
    }

    @GetMapping("/api/newest-five-invoices") // REST API endpoint
    @ResponseBody
    public ResponseEntity<List<NewestInvoiceDTO>> getFiveNewestInvoices() {
        List<Invoice> rawData = dashboardService.getFiveNewestInvoices(); // Fetch data
        List<NewestInvoiceDTO> response = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // For formatting dates

        for (Invoice invoice : rawData) {
            response.add(new NewestInvoiceDTO(
                invoice.getInvoiceNumber(),
                invoice.getInvoiceDate().format(formatter), // Format the date
                invoice.getCustomer() != null ? invoice.getCustomer().getName() : "Unknown", // Handle null customers
                invoice.getGrandTotal().doubleValue() // Convert BigDecimal to double
            ));
        }

        return new ResponseEntity<>(response, HttpStatus.OK); // Return JSON data
    }

    @GetMapping("/api/closest-due-five-invoices")
    public ResponseEntity<List<NewestInvoiceDTO>> getFiveDueClosestInvoices() {
        List<Invoice> rawData = dashboardService.getFiveDueClosestInvoices(); // Fetch sorted data
        List<NewestInvoiceDTO> response = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Format for dates

        for (int i = 0; i < Math.min(5, rawData.size()); i++) { // Get top 5 closest due dates
            Invoice invoice = rawData.get(i);
            response.add(new NewestInvoiceDTO(
                invoice.getInvoiceNumber(),
                invoice.getDueDate().format(formatter), // Format date
                invoice.getCustomer() != null ? invoice.getCustomer().getName() : "Unknown", // Handle null customers
                invoice.getGrandTotal().doubleValue() // Convert BigDecimal to double
            ));
        }

        return  new ResponseEntity<>(response, HttpStatus.OK); // Return JSON data
    }        
    

    
    
    // Dashboard manager finance //

    @GetMapping("/api/dashboard/invoices-per-month")
    @ResponseBody
    public ResponseEntity<List<InvoicePerMonthDTO>> getInvoicesPerMonth() {
        List<Object[]> invoiceData = dashboardService.getMonthlyInvoiceCounts();
        List<InvoicePerMonthDTO> invoicesPerMonth = new ArrayList<>();

        for (Month month : Month.values()) {
            invoicesPerMonth.add(new InvoicePerMonthDTO(month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), 0));
        }
        for (Object[] data : invoiceData) {
            int monthIndex = ((Number) data[0]).intValue() - 1; 
            int count = ((Number) data[1]).intValue();
            invoicesPerMonth.get(monthIndex).setCount(count);
        }
        return new ResponseEntity<>(invoicesPerMonth, HttpStatus.OK);
    }

    @GetMapping("/api/invoices/status-per-month")
    public ResponseEntity<List<MonthStatusDTO>> getInvoiceStatusPerMonth() {
        List<Object[]> results = dashboardService.getMonthlyInvoiceStatusCounts();
        Map<Integer, MonthStatusDTO> monthDataMap = new HashMap<>();
        for (Month month : Month.values()) {
            String monthName = month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            monthDataMap.put(month.getValue(), new MonthStatusDTO(monthName, 0, 0));
        }

        for (Object[] result : results) {
            int month = ((Number) result[0]).intValue();
            String status = (String) result[1];
            int count = ((Number) result[2]).intValue();

            MonthStatusDTO dto = monthDataMap.get(month);
            if ("Paid".equals(status)) {
                dto.setPaidInvoices(count);
            } else if ("Approved".equals(status)) {
                dto.setUnpaidInvoices(count);
            }
        }

        return ResponseEntity.ok(new ArrayList<>(monthDataMap.values()));
    }

    // Dashboard non-finance staff //

    @GetMapping("/api/invoices/latest-approved")
    @ResponseBody
    public ResponseEntity<List<NewestInvoiceDTO>> getLatestApprovedInvoices() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        List<Invoice> invoices = dashboardService.getTop5ApprovedInvoicesByStaffEmail(userEmail);
        List<NewestInvoiceDTO> invoicesDTO = invoices.stream()
        .map(invoice -> new NewestInvoiceDTO(
            invoice.getInvoiceNumber(),
            invoice.getApprovedDate().toString(),
            invoice.getCustomer().getName(),
            invoice.getGrandTotal().doubleValue()))
        .collect(Collectors.toList());

        return new ResponseEntity<>(invoicesDTO, HttpStatus.OK);
    }

    @GetMapping("/api/invoices/latest-need-revision")
    @ResponseBody
    public ResponseEntity<List<NewestInvoiceDTO>> getLatestNeedRevisionInvoices() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        List<Invoice> invoices = dashboardService.getTop5NeedRevisionInvoicesByStaffEmail(userEmail);
        List<NewestInvoiceDTO> invoicesDTO = invoices.stream()
        .map(invoice -> new NewestInvoiceDTO(
            invoice.getInvoiceNumber(),
            invoice.getInvoiceDate().toString(),
            invoice.getCustomer().getName(),
            invoice.getGrandTotal().doubleValue()))
        .collect(Collectors.toList());

        return new ResponseEntity<>(invoicesDTO, HttpStatus.OK);
    }

    @GetMapping("/api/invoices/due-by-email")
    @ResponseBody
    public ResponseEntity<List<NewestInvoiceDTO>> getFiveDueSoonInvoiceByEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        List<Invoice> invoices = dashboardService.getFiveDueClosestInvoicesByStaffEmail(userEmail);
        List<NewestInvoiceDTO> invoicesDTO = invoices.stream()
        .map(invoice -> new NewestInvoiceDTO(
            invoice.getInvoiceNumber(),
            invoice.getDueDate().toString(),
            invoice.getCustomer().getName(),
            invoice.getGrandTotal().doubleValue()))
        .collect(Collectors.toList());

        return new ResponseEntity<>(invoicesDTO, HttpStatus.OK);
    }
}
