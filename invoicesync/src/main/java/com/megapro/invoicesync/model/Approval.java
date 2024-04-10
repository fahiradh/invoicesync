package com.megapro.invoicesync.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "approval_flow")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Approval {
    private Employee user;
    private Invoice invoice;
    private ApprovalFlow approvalFlow;
    private int cycle;
    private String approvement_status;
    private String comment;
}
