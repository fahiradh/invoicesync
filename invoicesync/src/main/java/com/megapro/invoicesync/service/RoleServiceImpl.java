package com.megapro.invoicesync.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megapro.invoicesync.model.Role;
import com.megapro.invoicesync.repository.RoleDb;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService{
    @Autowired
    private RoleDb roleDb;

    @Override
    public List<Role> getAllList() {
        return roleDb.findAll();
    }

    @Override
    public Role getRoleByRoleName(String name) {
        Optional<Role> role = roleDb.findByRole(name);
        if (role.isPresent()) {
            return role.get();
        }
        return null;
    }

    @Override
    public void createRole(Role role) {
        roleDb.save(role);
    }
    
}
