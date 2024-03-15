package com.megapro.invoicesync.dto;
import org.mapstruct.Mapper;

import com.megapro.invoicesync.dto.request.CreateApprovalFlowRequest;
import com.megapro.invoicesync.model.ApprovalFlow;

@Mapper(componentModel="spring")
public interface ApprovalFlowMapper {
    ApprovalFlow createApprovalFlowRequestToApprovalFlow(CreateApprovalFlowRequest createApprovalFlowRequest);
}
