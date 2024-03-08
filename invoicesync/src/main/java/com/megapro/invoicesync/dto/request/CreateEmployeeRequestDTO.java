package com.megapro.invoicesync.dto.request;

import com.megapro.invoicesync.model.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateEmployeeRequestDTO {
    private String email;
    private String password;
    private Role role;
    private String no_hp;
    private String first_name;
    private String last_name;
    private String country;
    private String city;
    private String postCode;
    private String street;

}
