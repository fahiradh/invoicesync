package com.megapro.invoicesync.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import com.megapro.invoicesync.dto.TaxMapper;
import com.megapro.invoicesync.dto.request.CountTaxRequestDTO;
import com.megapro.invoicesync.dto.request.CreateTaxRequestDTO;
import com.megapro.invoicesync.dto.response.CountTaxResponseDTO;
import com.megapro.invoicesync.model.Tax;
import com.megapro.invoicesync.repository.UserAppDb;
import com.megapro.invoicesync.service.TaxService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class TaxRestController {
    @Autowired
    TaxService taxService;

    @Autowired
    TaxMapper taxMapper;

    @Autowired
    UserAppDb userAppDb;

    // @PostMapping("/count-tax")
    // public String countTax(CountTaxRequestDTO countRequest, Model model){
    //     var taxType = taxService.findById(countRequest.getId()).getTaxType();
    //     if(taxType.equals("include")){
    //         var countTaxResponse = taxService.countIncludedTaxes(countRequest);
    //         model.addAttribute("countResponse", countTaxResponse);
    //     } else {
    //         var countTaxResponse = taxService.countExcludedTaxes(countRequest);
    //         model.addAttribute("countResponse", countTaxResponse);
    //     }

    //     // TODO
    //     return "";
    // }

    @GetMapping("/api/taxes")
    public ResponseEntity<List<Tax>> getTaxes() {
        var taxes = taxService.getTaxes();
        System.out.println("Ini taxesnya " + taxes);
        return ResponseEntity.ok(taxes);
    }
    

    @PostMapping("/api/count-tax")
    public ResponseEntity<CountTaxResponseDTO> countTaxAPI(@RequestBody CountTaxRequestDTO countRequest){
        System.out.println("Ini tax request");
        System.out.println(countRequest);
        var countTaxResponse = taxService.countTax(countRequest);
        return ResponseEntity.ok(countTaxResponse);
    }
    
}

