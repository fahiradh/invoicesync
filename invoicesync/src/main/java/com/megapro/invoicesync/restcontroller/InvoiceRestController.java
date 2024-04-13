package com.megapro.invoicesync.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.megapro.invoicesync.dto.ApprovalFlowMapper;
import com.megapro.invoicesync.dto.request.CreateApprovalRequestDTO;
import com.megapro.invoicesync.model.Approval;
import com.megapro.invoicesync.repository.ApprovalDb;
import com.megapro.invoicesync.service.ApprovalFlowService;

@RestController
public class InvoiceRestController {
    @Autowired
    ApprovalDb approvalDb;

    @Autowired
    ApprovalFlowMapper approvalMapper;

    @Autowired
    ApprovalFlowService approvalService;

    @PostMapping("/invoice/add-approver")
    public ResponseEntity<String> addApprover(@RequestBody List<CreateApprovalRequestDTO> approvalListDTO){
        approvalService.saveApprover(approvalListDTO);
        return ResponseEntity.ok("berhasil");
    }
}
