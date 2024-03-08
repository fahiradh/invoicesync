package com.megapro.invoicesync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.megapro.invoicesync.dto.UserMapper;
import com.megapro.invoicesync.dto.request.CreateEmployeeRequestDTO;
import com.megapro.invoicesync.model.Employee;
import com.megapro.invoicesync.service.UserService;

import jakarta.validation.Valid;

@Controller
public class EmployeeController {
    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;
    
    @PostMapping("/create-account")
    public ResponseEntity<Employee> createEmployeeAccount(@Valid @RequestBody CreateEmployeeRequestDTO employeeDTO){
        var employee =  userMapper.createEmployeeRequestToEmployee(employeeDTO);
        userService.createEmployee(employee);
        return ResponseEntity.ok(employee);
    }
    
}
