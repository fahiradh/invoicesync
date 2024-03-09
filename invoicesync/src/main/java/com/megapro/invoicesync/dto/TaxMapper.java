package com.megapro.invoicesync.dto;

import org.mapstruct.Mapper;

import com.megapro.invoicesync.dto.request.CreateTaxRequestDTO;
import com.megapro.invoicesync.model.Tax;

@Mapper(componentModel = "spring")
public interface TaxMapper {
    Tax createTaxRequestToTax(CreateTaxRequestDTO createTaxRequest);
}
