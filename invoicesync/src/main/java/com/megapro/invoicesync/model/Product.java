package com.megapro.invoicesync.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID productId;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="description", nullable=false)
    private String description;

    @Column(name="quantity")
    private int quantity;

    @Column(name="price")
    private long price;

    @Column(name="discount")
    private int discount;

    @Column(name="total_price")
    private long totalPrice;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @Column(name="tax")
    private int tax;
}

