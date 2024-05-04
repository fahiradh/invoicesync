package com.megapro.invoicesync.service;

import com.megapro.invoicesync.repository.ApprovalDb;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.megapro.invoicesync.model.Approval;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.UserApp;

import java. util.List;

@Transactional
@Service
public class ApprovalServiceImpl implements ApprovalService{
    @Autowired
    private ApprovalDb approvalDb;

    @Autowired
    private InvoiceService invoiceService;
    
    @Override
    public List<Approval> findApproversByInvoice(Invoice invoice) {
        // // Decode the invoice number if it's URL-encoded or formatted differently
        // String decodedInvoiceNumber = decodeInvoiceNumber(invoiceNumber);

        // // Retrieve the invoice entity using the invoice number
        // Invoice invoice = invoiceService.getInvoiceByInvoiceNumber(decodedInvoiceNumber);
        
        // if (invoice == null) {
        //     throw new EntityNotFoundException("Invoice with number: " + decodedInvoiceNumber + " not found");
        // }
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice must not be null");
        }
        // Now use the invoice entity to find all Approval entities associated with it
        return approvalDb.findByInvoice(invoice);
    }

    private String decodeInvoiceNumber(String encodedInvoiceNumber) {
        // Implement any necessary decoding logic here, such as replacing underscores with slashes
        return encodedInvoiceNumber.replace('_', '/');
    }

    @Override
    public Approval findApprovalByApprovalId(int id) {
        return approvalDb.findByApprovalId(id);
    }

    @Override
    public void saveApproval(Approval approval) {
        approvalDb.save(approval);
    }

    @Override
    public List<UserApp> getEligibleApproversForInvoice(Invoice invoice) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEligibleApproversForInvoice'");
    }
}
