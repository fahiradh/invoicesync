package com.megapro.invoicesync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.megapro.invoicesync.model.ApprovalFlow;

@Repository
public interface ApprovalFlowDb extends JpaRepository<ApprovalFlow, Long> {
    // You can add custom database queries if needed
}

