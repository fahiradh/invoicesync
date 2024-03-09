package com.megapro.invoicesync.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.megapro.invoicesync.dto.ApprovalFlowMapper;
import com.megapro.invoicesync.dto.request.CreateApprovalFlowRequest;
import com.megapro.invoicesync.model.ApprovalFlow;
import com.megapro.invoicesync.repository.UserAppDb;
import com.megapro.invoicesync.service.ApprovalFlowService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;


@Controller
public class ApprovalFlowController {

    @Autowired
    private ApprovalFlowService approvalFlowService;

    @Autowired 
    ApprovalFlowMapper approvalFlowMapper;

    @Autowired
    private UserAppDb userAppDb;

    @PostMapping(value="/add-approval-flow")
    public String createFlow(@Valid CreateApprovalFlowRequest approvalFlowDTO, BindingResult result, Model model, RedirectAttributes redirectAttributes){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();
        model.addAttribute("role", role);
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Terdapat kesalahan dalam input. Silakan periksa kembali.");
            return "redirect:/approval-flows"; // Ganti "nama-view-anda" dengan nama file HTML Anda
        }
        var approvalFlow = approvalFlowMapper.createApprovalFlowRequestToApprovalFlow(approvalFlowDTO);
        approvalFlowService.createApprovalFlow(approvalFlow);
        redirectAttributes.addFlashAttribute("successMessage", "Flow berhasil ditambahkan.");
        return "redirect:/approval-flows";
    }
    
    @GetMapping("/approval-flows")
    public String getAllFlow(Model model, @ModelAttribute("successMessage") String successMessage,
                                @ModelAttribute("errorMessage") String errorMessage) {
                                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                                String email = authentication.getName();
                                var user = userAppDb.findByEmail(email);
                                String role = user.getRole().getRole();
                                model.addAttribute("role", role);
        model.addAttribute("approvalFlow", new CreateApprovalFlowRequest());
        List<ApprovalFlow> listApproval = approvalFlowService.getAllApprovalFlows();
        model.addAttribute("listApproval", listApproval);
        model.addAttribute("successMessage", successMessage);
        model.addAttribute("errorMessage", errorMessage);
        return "approval-hierarchy";
    }
}
