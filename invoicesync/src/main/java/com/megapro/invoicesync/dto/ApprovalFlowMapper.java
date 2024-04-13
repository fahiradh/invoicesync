package com.megapro.invoicesync.dto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.megapro.invoicesync.dto.request.CreateApprovalFlowRequest;
import com.megapro.invoicesync.dto.request.CreateApprovalRequestDTO;
import com.megapro.invoicesync.model.Approval;
import com.megapro.invoicesync.model.ApprovalFlow;
import com.megapro.invoicesync.repository.EmployeeDb;
import com.megapro.invoicesync.service.UserService;

import java.util.List;

@Mapper(componentModel="spring")
public interface ApprovalFlowMapper {
    ApprovalFlow createApprovalFlowRequestToApprovalFlow(CreateApprovalFlowRequest createApprovalFlowRequest);

    // punya approval coba-coba
    Approval CreateApprovalRequestDTOToApproval(CreateApprovalRequestDTO createApprovalRequestDTO);
    List<Approval> CreateApprovalRequestDTOToApproval(List<CreateApprovalRequestDTO> createApprovalRequestDTO);

}
