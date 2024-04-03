package com.megapro.invoicesync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.megapro.invoicesync.model.ApprovalFlow;
import com.megapro.invoicesync.repository.ApprovalFlowDb;

import java.util.Comparator;
import java.util.List;

@Transactional
@Service
public class ApprovalFlowServiceImpl implements ApprovalFlowService {

    @Autowired
    private ApprovalFlowDb approvalFlowDb;

    @Override
    
    public void createApprovalFlow(ApprovalFlow approvalFlow) throws IllegalArgumentException {
        // Cek apakah ada approval flow yang sudah ada
        List<ApprovalFlow> existingFlows = approvalFlowDb.findAll();
        
        // Sort existing flows berdasarkan nominal range secara ascending
        existingFlows.sort(Comparator.comparingLong(ApprovalFlow::getNominalRange));
        
        // Jika ada existing flow, pastikan nominal baru lebih besar dari nominal terakhir
        if (!existingFlows.isEmpty()) {
            ApprovalFlow lastFlow = existingFlows.get(existingFlows.size() - 1);
            if (approvalFlow.getNominalRange() <= lastFlow.getNominalRange()) {
                throw new IllegalArgumentException("Nominal pada flow baru harus lebih besar dari flow sebelumnya.");
            }
        }

        approvalFlowDb.save(approvalFlow);
    }

    @Override
    public List<ApprovalFlow> getAllApprovalFlows() {
        return approvalFlowDb.findAll();
    }

    @Override
    @Transactional
    public void resetAllApprovalFlows() {
        approvalFlowDb.deleteAll();
    }

}
