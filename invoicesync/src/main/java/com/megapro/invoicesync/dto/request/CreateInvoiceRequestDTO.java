package com.megapro.invoicesync.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInvoiceRequestDTO {
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private int totalDiscount;
    private long totalTax;
    private long grandTotal;
    private String totalWords;
    private String signatureBg;
    private String signature;
    private String addedDocument;
    private String customerName;
    private String customerAddress;
    private String customerContact;
    private String currency;
    private String tnc;
    private int additionalDiscount;
    private LocalDateTime created = LocalDateTime.now();
    private String status = "Pending Approval";
}
