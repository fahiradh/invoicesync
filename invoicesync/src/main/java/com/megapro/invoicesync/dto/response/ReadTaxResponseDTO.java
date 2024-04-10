package com.megapro.invoicesync.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ReadTaxResponseDTO {
    private int taxId;
    private String taxName;
    private String taxType;
    private int taxPercentage;
}
