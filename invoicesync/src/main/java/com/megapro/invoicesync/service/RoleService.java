package com.megapro.invoicesync.service;

import java.util.List;

import com.megapro.invoicesync.model.Role;

public interface RoleService {
    List<Role> getAllList();
    Role getRoleByRoleName(String name);
    void createRole(Role role);
}
