package com.megapro.invoicesync.dto;

import org.mapstruct.Mapper;

import com.megapro.invoicesync.dto.request.CreateInvoiceRequestDTO;
import com.megapro.invoicesync.model.Invoice;

@Mapper(componentModel="spring")
public interface InvoiceMapper {
    Invoice createInvoiceRequestToInvoice(CreateInvoiceRequestDTO CreateInvoiceRequest);
    
}

