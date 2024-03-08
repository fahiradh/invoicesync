package com.megapro.invoicesync.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "customer")
@PrimaryKeyJoinColumn(name = "user_id")
public class Customer extends UserApp implements Serializable{
    @Column(name="address")
    private String address;

    @Column(name="contact")
    private String contact;

    @OneToMany(mappedBy="invoiceId", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<Invoice> invoice;
}
