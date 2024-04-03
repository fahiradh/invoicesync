package com.megapro.invoicesync.service;

import java.util.List;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.megapro.invoicesync.dto.request.CreateUserAppRequestDTO;
import com.megapro.invoicesync.model.Employee;
import com.megapro.invoicesync.model.UserApp;
import com.megapro.invoicesync.repository.EmployeeDb;
import com.megapro.invoicesync.repository.UserAppDb;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService{
    @Autowired
    private UserAppDb userDb;

    @Autowired
    private EmployeeDb employeeDb;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Override
    public void createUserApp(UserApp user, CreateUserAppRequestDTO userDTO) {
        user.setRole(roleService.getRoleByRoleName(userDTO.getRole().getRole()));
        String hashedPass = encoder.encode(user.getPassword());
        user.setPassword(hashedPass);
        userDb.save(user);
    }

    @Override
    public void createEmployee(Employee employee) {
        String hashedPass = encoder.encode(employee.getPassword());
        employee.setPassword(hashedPass);
        employeeDb.save(employee);
    }

    @Override
    public Employee findByEmail(String email) {
        if (employeeDb.findByEmail(email).isDeleted() != true) {
            return employeeDb.findByEmail(email);
        } 
        return null;
    }

    @Override
    public List<Employee> getAllEmployee() {
        return employeeDb.findByDeletedFalse();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userDb.existsByEmail(email);
    }

    @Override
    public boolean existsByNomorHp(String phoneNumber) {
        return employeeDb.existsByNomorHp(phoneNumber);
    }

    @Override
    public Employee getEmployeeById(UUID id) {
        return employeeDb.findEmployeeByUserId(id);
    }

    @Override
    public void deleteEmployee(Employee employee) {
        employee.setDeleted(true);
        employeeDb.save(employee);
    }
}
