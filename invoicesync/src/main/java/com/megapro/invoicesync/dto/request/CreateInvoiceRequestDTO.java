package com.megapro.invoicesync.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInvoiceRequestDTO {
    private LocalDate invoiceDate = LocalDate.now();
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private String totalWords;
    private String signature;
    private String city;
    private BigDecimal subtotal;
    private int totalDiscount = 0;
    private String accountNumber;
    private String bankName;
    private String accountName;
    private String status = "Draft";
    private UUID customerId;
    private String productDocument;
    private String additionalDocument;
    private String staffEmail;
}
