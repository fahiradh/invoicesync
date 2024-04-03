package com.megapro.invoicesync.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import com.megapro.invoicesync.model.Customer;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReadCustomerResponseDTO {
    private UUID customerId;
    private String name;
    private String address;
    private String contact;
    private String phone;
}
