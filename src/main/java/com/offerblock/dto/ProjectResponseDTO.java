package com.offerblock.dto;

import com.offerblock.entity.Budget;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProjectResponseDTO {

	private Long projectId;
	private String projectName;
	private Budget budget;
	private List<DepartmentDTO> departments;
	private List<RecruiterDTO> projectRecruitors;
	private int offerCount;
	private int hiringCount;
	private String status;

	public ProjectResponseDTO(Long projectId, String projectName, Budget budget, List<DepartmentDTO> departments,
			List<RecruiterDTO> projectRecruitors, int offerCount, int hiringCount, String status) {
		super();
		this.projectId = projectId;
		this.projectName = projectName;
		this.budget = budget;
		this.departments = departments;
		this.projectRecruitors = projectRecruitors;
		this.offerCount = offerCount;
		this.hiringCount = hiringCount;
		this.status = status;
	}

}
