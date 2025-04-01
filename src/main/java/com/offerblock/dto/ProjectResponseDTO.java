package com.offerblock.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.offerblock.entity.Budget;
import com.offerblock.entity.Project;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProjectResponseDTO {

	private Long projectId;
	private String projectName;
	private Budget budget;
	private List<DepartmentDTO> departments;
	private List<RecruiterDTO> projectHRs;
	private int offerCount;
	private int hiringCount;
	private String status;

	public ProjectResponseDTO(Long projectId, String projectName, Budget budget, List<DepartmentDTO> departments,
			List<RecruiterDTO> projectHRs, int offerCount, int hiringCount, String status) {
		super();
		this.projectId = projectId;
		this.projectName = projectName;
		this.budget = budget;
		this.departments = departments;
		this.projectHRs = projectHRs;
		this.offerCount = offerCount;
		this.hiringCount = hiringCount;
		this.status = status;
	}

	public ProjectResponseDTO(Project project) {
		this.projectId = project.getProjectId();
		this.projectName = project.getProjectName();

		// Assigning the budget (assuming you want to keep it as a Budget object)
		this.budget = project.getBudget() != null ? project.getBudget() : new Budget(); // Handle null case for Budget

		// Assigning status
		this.status = project.getStatus() != null ? project.getStatus().name() : "Not Assigned";

		// Convert Department entities to DepartmentDTOs
		this.departments = project.getDepartments().stream()
				.map(department -> new DepartmentDTO(department.getDepartmentName(),
						department.getAssignedHRs().stream()
								.map(recruiter -> new RecruiterDTO(recruiter.getId(), recruiter.getName(),
										recruiter.getDesiganation()))
								.collect(Collectors.toList()),
						department.getPositions().stream().map(position -> new PositionDTO(position)) // Assuming
																										// PositionDTO
																										// conversion
								.collect(Collectors.toList())))
				.collect(Collectors.toList());

		// If you need the HRs at project level (assuming they are stored in projectHRs
		// field)
		this.projectHRs = project.getProjectHRs().stream()
				.map(recruiter -> new RecruiterDTO(recruiter.getId(), recruiter.getName(), recruiter.getDesiganation()))
				.collect(Collectors.toList());

		// Calculate the hiring count (implement your logic here)
		this.hiringCount = project.getHiringCount(); // Use your actual logic for this field
	}

}
