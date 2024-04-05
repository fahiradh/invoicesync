package com.megapro.invoicesync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.megapro.invoicesync.dto.TaxMapper;
import com.megapro.invoicesync.dto.request.CountTaxRequestDTO;
import com.megapro.invoicesync.dto.request.CreateTaxRequestDTO;
import com.megapro.invoicesync.repository.UserAppDb;
import com.megapro.invoicesync.service.TaxService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class TaxController {
    @Autowired
    TaxService taxService;

    @Autowired
    TaxMapper taxMapper;

    @Autowired
    UserAppDb userAppDb;

    @GetMapping("/tax")
    public String viewTax(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();
        model.addAttribute("role", role);
        
        var taxDTO = new CreateTaxRequestDTO();
        model.addAttribute("taxDTO", taxDTO);

        var taxes = taxService.getTaxes();
        model.addAttribute("taxes", taxes);
        return "tax/tax";
    }
    
    @PostMapping("/create-tax")
    public RedirectView createTax(CreateTaxRequestDTO taxRequest, Model model) {
        var tax = taxMapper.createTaxRequestToTax(taxRequest);
        taxService.save(tax);

        var taxDTO = new CreateTaxRequestDTO();
        model.addAttribute("taxDTO", taxDTO);

        var taxes = taxService.getTaxes();
        model.addAttribute("taxes", taxes);
        return new RedirectView("/tax");
    }
    
}
