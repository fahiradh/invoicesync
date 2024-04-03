package com.megapro.invoicesync.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megapro.invoicesync.model.Employee;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface EmployeeDb extends JpaRepository<Employee, UUID>{
    Employee findEmployeeByUserId(UUID id);   
    Employee findByEmail(String email); 
    boolean existsByNomorHp(String nomorHp);
    List<Employee> findByDeletedFalse();
}
