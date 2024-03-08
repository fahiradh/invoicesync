package com.megapro.invoicesync.service;

import com.megapro.invoicesync.model.Invoice;
import java.util.List;

public interface InvoiceService {
    void createInvoice(Invoice invoice);
    void attributeInvoce(Invoice invoice);
    long countInvoice();
}
