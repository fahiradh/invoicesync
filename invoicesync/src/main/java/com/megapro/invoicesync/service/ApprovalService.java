package com.megapro.invoicesync.service;

import java.util.List;

import com.megapro.invoicesync.model.Approval;
import com.megapro.invoicesync.model.Invoice;

public interface ApprovalService {
    public List<Approval> findApproversByInvoice(Invoice invoice);
    
}
