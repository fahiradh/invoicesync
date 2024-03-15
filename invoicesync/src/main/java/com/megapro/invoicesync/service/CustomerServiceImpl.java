package com.megapro.invoicesync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megapro.invoicesync.model.Customer;
import com.megapro.invoicesync.repository.CustomerDb;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService{
    @Autowired
    CustomerDb customerDb;

    @Override
    public void createCustomer(Customer customer) {
        customerDb.save(customer);
    }

    @Override
    public List<Customer> getAllCustomer() {
        return customerDb.findAll();
    }
}
