package com.megapro.invoicesync.dto.request;

import java.util.UUID;

import com.megapro.invoicesync.model.Employee;
import com.megapro.invoicesync.model.Invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateApprovalRequestDTO {
    private UUID employeeId;
    private UUID invoiceId;
    private int approvalRank;
    private int cycle;
    private String approvalStatus;
    private String comment;
}
