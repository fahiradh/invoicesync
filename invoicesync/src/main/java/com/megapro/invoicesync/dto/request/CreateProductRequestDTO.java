package com.megapro.invoicesync.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequestDTO {
    private String name;
    private String description;
    private int quantity;
    private long price;
    private int discount;
    private String totalPrice;
}