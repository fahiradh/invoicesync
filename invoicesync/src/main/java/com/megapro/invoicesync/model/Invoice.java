package com.megapro.invoicesync.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

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

    @Lob
    @Column(name="signature")
    private String signature;

    @Column(name="city")
    private String city;

    @Column(name="product_document")
    private String productDocument;

    @Column(name="subtotal")
    private BigDecimal subtotal;

    @Column(name="total_discount")
    private int totalDiscount;

    @ManyToMany
    @JoinTable(name = "invoice_tax", joinColumns = @JoinColumn(name = "invoice_id"),
            inverseJoinColumns = @JoinColumn(name = "tax_id"))
    List<Tax> listTax;

    @Column(name="total_tax")
    private BigDecimal taxTotal;

    @Column(name="grand_total")
    private BigDecimal grandTotal;

    @Column(name="account_number")
    private String accountNumber;

    @Column(name="bank_name")
    private String bankName;

    @Column(name="account_name")
    private String accountName;

    @Column(name="additional_document")
    private String additionalDocument;

    // @OneToMany(mappedBy="asApprover", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    // private List<Employee> asApprover;

    @OneToMany(mappedBy = "productId", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<Product> listProduct;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="customer_id")
    private Customer customer;

    @Column(name="status")
    private String status;

    @OneToMany(mappedBy = "invoice")
    @OrderBy("approval_id ASC")
    private List<Approval> listApproval;
}
