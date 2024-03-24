package com.megapro.invoicesync.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import com.megapro.invoicesync.model.Customer;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReadInvoiceResponse {
    private UUID invoiceId;
    private String staffEmail;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private String totalWords;
    private String signature;
    private String city;
    private String productDocument;
    private BigDecimal subtotal;
    private int totalDiscount;
    private String accountNumber;
    private String bankName;
    private String accountName;
    private String additionalDocument;
    private Customer customer;
    private String status;
    private String staffRole;
}
