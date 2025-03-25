package com.offerblock.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DepartmentDTO {

	private String departmentName;
	private List<RecruiterDTO> assignedHRs; // Full HR details
	private List<PositionDTO> positions;
}
