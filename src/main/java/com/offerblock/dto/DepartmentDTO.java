package com.offerblock.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDTO {

	private String departmentName;
	private List<RecruiterDTO> assignedHRs; 
	private List<PositionDTO> positions;
	
}