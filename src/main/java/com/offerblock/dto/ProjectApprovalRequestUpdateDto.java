package com.offerblock.dto;

import com.offerblock.enums.ApprovalStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectApprovalRequestUpdateDto {
	
    private ApprovalStatus approvalStatus;  
    private String comments;  // Optional comments field
}
