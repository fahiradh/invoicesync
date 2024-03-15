package com.megapro.invoicesync.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="invoice_id")
    private UUID invoiceId;

    @Column(name="staff_email")
    private String staffEmail;

    @Column(name="invoice_number")
    private String invoiceNumber;

    @Column(name="invoice_date")
    private LocalDate invoiceDate;

    @Column(name="due_date")
    private LocalDate dueDate;

    @Column(name="total_words")
    private String totalWords;

    @Column(name="signature_bg")
    private String signatureBg;

    @Column(name="signature")
    private String signature;

    @Column(name="added_document")
    private String addedDocument;

    @Column(name="currency")
    private String currency;

    @Column(name="tnc")
    private String tnc;

    @Column(name="subtotal")
    private BigDecimal subtotal;

    @Column(name="total_discount")
    private int totalDiscount;

    @Column(name="totalTax")
    private long totalTax;

    @Column(name="additional_discount")
    private int additionalDiscount;

    @Column(name="created")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    // @OneToMany(mappedBy="asApprover", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    // private List<Employee> asApprover;

    @OneToMany(mappedBy = "productId", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<Product> listProduct;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="customer_id")
    private Customer customer;

    @Column(name="status")
    private String status;
}
