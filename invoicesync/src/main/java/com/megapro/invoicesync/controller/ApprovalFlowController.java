package com.megapro.invoicesync.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import com.megapro.invoicesync.dto.ApprovalFlowMapper;
import com.megapro.invoicesync.dto.request.CreateApprovalFlowRequest;
import com.megapro.invoicesync.model.ApprovalFlow;
import com.megapro.invoicesync.service.ApprovalFlowService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class ApprovalFlowController {

    @Autowired
    private ApprovalFlowService approvalFlowService;

    @Autowired ApprovalFlowMapper approvalFlowMapper;


    @PostMapping(value="/add-approval-flow")
    public ApprovalFlow restCreateFlow(@Valid @RequestBody CreateApprovalFlowRequest approvalFlowDTO, Model model){
        var approvalFlow = approvalFlowMapper.createApprovalFlowRequestToApprovalFlow(approvalFlowDTO);
        approvalFlowService.createApprovalFlow(approvalFlow);
        model.addAttribute("approvalFlow", approvalFlow);
        return approvalFlow;
    }



    @GetMapping("/approval-flow")
    public String getAllFlow(Model model) {
        List<ApprovalFlow> listApproval = approvalFlowService.getAllApprovalFlows();

        model.addAttribute("listApproval", listApproval);

        return  "approval-hierarchy";
    }
    



    
}
