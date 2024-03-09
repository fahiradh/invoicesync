package com.megapro.invoicesync.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megapro.invoicesync.dto.request.CountTaxRequestDTO;
import com.megapro.invoicesync.dto.response.CountTaxResponseDTO;
import com.megapro.invoicesync.model.Tax;
import com.megapro.invoicesync.repository.TaxDb;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TaxService {
    @Autowired
    TaxDb taxDB;

    public Tax save(Tax tax){
        return taxDB.save(tax);
    }

    public List<Tax> getTaxes(){
        return taxDB.findAll();
    }

    public Tax findById(int id){
        return taxDB.findByTaxId(id);
    }

    public CountTaxResponseDTO countIncludedTaxes(CountTaxRequestDTO request){
        var amount = request.getAmount();
        var tax = taxDB.findByTaxId(request.getId());

        var invoiceNominal = amount * 10 / tax.getTaxPercentage();
        var taxNominal = invoiceNominal - amount;

        return new CountTaxResponseDTO(invoiceNominal, taxNominal);
    }

    public CountTaxResponseDTO countExcludedTaxes(CountTaxRequestDTO request){
        var amount = request.getAmount();
        var tax = taxDB.findByTaxId(request.getId());

        var invoiceNominal = amount * 10 / tax.getTaxPercentage();
        var taxNominal = amount-invoiceNominal;

        return new CountTaxResponseDTO(invoiceNominal, taxNominal);
    }
}
