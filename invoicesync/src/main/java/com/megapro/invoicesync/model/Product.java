package com.megapro.invoicesync.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="product_id")
    private Long productId;

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

    @Column(name="subtotal")
    private long subtotal;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @Column(name="tax")
    private int tax;

    @Column(name="created")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    // @OneToMany(mappedBy="product", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    // private List<Tax> tax;
}

