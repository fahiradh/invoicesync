package com.megapro.invoicesync.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.megapro.invoicesync.dto.request.CreateInvoiceRequestDTO;
import com.megapro.invoicesync.dto.response.ReadInvoiceResponse;
import com.megapro.invoicesync.model.Invoice;

@Mapper(componentModel="spring")
public interface InvoiceMapper {
    Invoice createInvoiceRequestToInvoice(CreateInvoiceRequestDTO CreateInvoiceRequest);
    ReadInvoiceResponse readInvoiceToInvoiceResponse(Invoice invoice);
}

