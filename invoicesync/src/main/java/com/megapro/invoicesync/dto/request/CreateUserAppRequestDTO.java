package com.megapro.invoicesync.dto.request;

import com.megapro.invoicesync.model.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateUserAppRequestDTO {
    private String email;
    private String password;
    private String role = "Admin";
}

