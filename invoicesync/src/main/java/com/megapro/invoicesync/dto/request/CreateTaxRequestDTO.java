package com.megapro.invoicesync.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateTaxRequestDTO {
    
    private String taxName;
    private String taxType;
    private int taxPercentage;
}
