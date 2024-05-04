package com.megapro.invoicesync.service;

import java.util.List;

import com.megapro.invoicesync.model.Approval;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.UserApp;

public interface ApprovalService {
    public List<Approval> findApproversByInvoice(Invoice invoice);
    public Approval findApprovalByApprovalId(int id);
    public void saveApproval(Approval approval);
    public List<UserApp> getEligibleApproversForInvoice(Invoice invoice);
}
