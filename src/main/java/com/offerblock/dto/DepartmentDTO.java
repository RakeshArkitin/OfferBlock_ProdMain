package com.offerblock.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.offerblock.entity.Department;

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
	
	public DepartmentDTO(Department department) {
        this.departmentName = department.getDepartmentName();
        this.assignedHRs = department.getAssignedHRs().stream()
            .map(RecruiterDTO::new) // Assuming RecruiterDTO has a constructor accepting Recruiter
            .collect(Collectors.toList());
        this.positions = department.getPositions().stream()
            .map(PositionDTO::new) // Assuming PositionDTO has a constructor accepting Position
            .collect(Collectors.toList());
    }
	
}
