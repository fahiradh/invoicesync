package com.megapro.invoicesync.service;

import com.megapro.invoicesync.model.Customer;
import java.util.List;

public interface CustomerService {
    void createCustomer(Customer customer);
    List<Customer> getAllCustomer();
}