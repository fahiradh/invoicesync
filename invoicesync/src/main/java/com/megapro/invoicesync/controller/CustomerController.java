package com.megapro.invoicesync.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.megapro.invoicesync.dto.CustomerMapper;
import com.megapro.invoicesync.dto.request.CreateCustomerRequestDTO;
import com.megapro.invoicesync.dto.request.CreateInvoiceRequestDTO;
import com.megapro.invoicesync.model.Customer;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.repository.UserAppDb;
import com.megapro.invoicesync.service.CustomerService;

@Controller
public class CustomerController {
    @Autowired
    UserAppDb userAppDb;

    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerMapper customerMapper;

    @GetMapping("/create-customer")
    public String formCreateCustomer(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();
        var customerDTO = new CreateCustomerRequestDTO();
        model.addAttribute("email", email);
        model.addAttribute("role", role);
        model.addAttribute("customerDTO", customerDTO);
        return "customer/form-create-customer";
    }
    
    @PostMapping("/create-customer")
    public RedirectView createCustomer(@ModelAttribute CreateCustomerRequestDTO customerRequest, Model model){
        var customer = customerMapper.createCustomerDTOToCustomer(customerRequest);
        customerService.createCustomer(customer);
        return new RedirectView("/create-invoice");
    }

    @GetMapping(value = "/api/v1/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Customer> getCustomers() {
        List<Customer> listCustomers = customerService.getAllCustomer();
        return listCustomers;
    }
}
