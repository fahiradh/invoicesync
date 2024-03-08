package com.megapro.invoicesync.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megapro.invoicesync.model.Employee;

import jakarta.transaction.Transactional;


@Repository
@Transactional
public interface EmployeeDb extends JpaRepository<Employee,UUID>{
    Employee findEmployeeByUserId(UUID id);   
    Employee findByEmail(String email); 
    //List<Employee> findAll();
}
