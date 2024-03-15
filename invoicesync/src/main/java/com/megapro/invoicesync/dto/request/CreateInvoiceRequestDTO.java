package com.megapro.invoicesync.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInvoiceRequestDTO {
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private int totalDiscount;
    private BigDecimal subtotal;
    private long totalTax;
    private String totalWords;
    private String signatureBg;
    private String signature;
    private String addedDocument;
    private String customerName;
    private String customerAddress;
    private String customerContact;
    private String currency = "IDR";
    private String tnc;
    private int additionalDiscount;
    private LocalDateTime created = LocalDateTime.now();
    private String status = "Draft";
}
