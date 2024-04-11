package com.megapro.invoicesync.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "approval")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int approval_id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "approval_user")
    private Employee employee;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "approval_invoice")
    private Invoice invoice;

    @Column(name="rank")
    private int rank;

    @Column(name="cycle")
    private int cycle;

    @Column(name="approval_status")
    private String approvalStatus;

    @Column(name="comment")
    private String comment;
}
