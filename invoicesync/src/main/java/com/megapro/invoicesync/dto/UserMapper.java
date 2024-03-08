package com.megapro.invoicesync.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.megapro.invoicesync.dto.request.CreateEmployeeRequestDTO;
import com.megapro.invoicesync.dto.request.CreateUserAppRequestDTO;
import com.megapro.invoicesync.model.Employee;
import com.megapro.invoicesync.model.UserApp;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "role", ignore = true)
    Employee createEmployeeRequestToEmployee(CreateEmployeeRequestDTO createEmployeeRequestDTO);

    @Mapping(target = "role", ignore = true)
    UserApp createUserAppRequestDTOToUserApp(CreateUserAppRequestDTO createUserRequestDTO);
}
