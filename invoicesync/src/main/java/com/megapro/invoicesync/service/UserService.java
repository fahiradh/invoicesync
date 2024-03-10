package com.megapro.invoicesync.service;

import java.util.List;

import com.megapro.invoicesync.dto.request.CreateUserAppRequestDTO;
import com.megapro.invoicesync.model.Employee;
import com.megapro.invoicesync.model.UserApp;

public interface UserService {
    void createUserApp(UserApp user, CreateUserAppRequestDTO userDTO);
    void createEmployee(Employee employee);
    Employee findByEmail(String email);
    List<Employee> getAllEmployee();
    boolean existsByEmail(String email);
    boolean existsByNomorHp(String phoneNumber);
}
