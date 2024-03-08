package com.megapro.invoicesync.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tax")
public class Tax {
    @Id
    @NotNull
    @Column(name = "tax_id")
    private int taxId;

    @NotNull
    @Column(name = "tax_name")
    private String taxName;

    @NotNull
    @Column(name = "tax_type")
    private String taxType;

    @NotNull
    @Column(name = "tax_percentage")
    private int taxPercentage;

    // @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    // @JoinColumn(name = "product_id")
    // private Product product;
    
}
