package com.offerblock.dto;

import com.offerblock.entity.BudgetSanctionRequest;

public class BudgetSanctionRequestDto {

	private Long id;
	private Long projectId;
	private String companyId;
	private String companyName;
	private String sanctionerId;
	private String status;
	private ProjectResponseDTO project;

	public BudgetSanctionRequestDto(BudgetSanctionRequest request) {
		this.id = request.getId();
		this.projectId = request.getProject().getProjectId();
		this.project = new ProjectResponseDTO(request.getProject());
		this.companyId = request.getCompany().getCompanyId();
		this.companyName = request.getCompany().getCompanyName();
		this.sanctionerId = request.getSanctioner() != null ? request.getSanctioner().getCandidate().getCandidateId()
				: null;
		this.status = request.getStatus().name();
	}

	public Long getId() {
		return id;
	}

	public Long getProjectId() {
		return projectId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getSanctionerId() {
		return sanctionerId;
	}

	public String getStatus() {
		return status;
	}

	public ProjectResponseDTO getProject() {
		return project;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setSanctionerId(String sanctionerId) {
		this.sanctionerId = sanctionerId;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setProject(ProjectResponseDTO project) {
		this.project = project;

	}

}
