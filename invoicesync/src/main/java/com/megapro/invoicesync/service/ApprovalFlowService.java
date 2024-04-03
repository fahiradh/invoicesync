package com.megapro.invoicesync.service;

import java.util.List;

import com.megapro.invoicesync.model.ApprovalFlow;

public interface ApprovalFlowService {
    void createApprovalFlow(ApprovalFlow approvalFlow);
    List<ApprovalFlow> getAllApprovalFlows();
    void resetAllApprovalFlows();
}
