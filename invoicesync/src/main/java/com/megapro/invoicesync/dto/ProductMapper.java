package com.megapro.invoicesync.dto;

import org.mapstruct.Mapper;

import com.megapro.invoicesync.dto.request.CreateProductRequestDTO;
import com.megapro.invoicesync.model.Product;

@Mapper(componentModel="spring")
public interface ProductMapper {
    Product createProductRequestToProduct(CreateProductRequestDTO productDTO);
}
