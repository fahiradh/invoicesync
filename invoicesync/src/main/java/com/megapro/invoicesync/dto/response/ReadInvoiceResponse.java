package com.megapro.invoicesync.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReadInvoiceResponse {
    private UUID invoiceId;
    private String invoiceNumber;
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
    private String staffRole;
}
