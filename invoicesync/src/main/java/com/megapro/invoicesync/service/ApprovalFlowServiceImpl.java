package com.megapro.invoicesync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.megapro.invoicesync.model.ApprovalFlow;
import com.megapro.invoicesync.repository.ApprovalFlowDb;

import java.util.List;
import java.util.UUID;

@Service
public class ApprovalFlowServiceImpl implements ApprovalFlowService {

    @Autowired
    private ApprovalFlowDb approvalFlowDb;

    @Override
    @Transactional
    public void createApprovalFlow(ApprovalFlow approvalFlow) {
        approvalFlowDb.save(approvalFlow);

    }
        

    @Override
    public List<ApprovalFlow> getAllApprovalFlows() {
        return approvalFlowDb.findAll();
    }
}
