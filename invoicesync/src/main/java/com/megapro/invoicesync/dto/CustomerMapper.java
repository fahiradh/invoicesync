package com.megapro.invoicesync.dto;

import org.mapstruct.Mapper;
import com.megapro.invoicesync.dto.request.CreateCustomerRequestDTO;
import com.megapro.invoicesync.model.Customer;

@Mapper(componentModel="spring")
public interface CustomerMapper {
    Customer createCustomerDTOToCustomer(CreateCustomerRequestDTO customerDTO);
}
