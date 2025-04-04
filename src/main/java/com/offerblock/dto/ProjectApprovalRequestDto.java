package com.offerblock.dto;

import com.offerblock.entity.ProjectApprovalRequest;

public class ProjectApprovalRequestDto {

	private Long id;
	private Long projectId;
	private String requestedBy;
	private String companyId;
	private String companyName;
	private String approverId;
	private String status;
	private ProjectResponseDTO project;

	public ProjectApprovalRequestDto(ProjectApprovalRequest request) {
		this.id = request.getId();
		this.projectId = request.getProject().getProjectId();
		this.project = new ProjectResponseDTO(request.getProject());
		this.requestedBy = request.getRequestedBy();
		this.companyId = request.getCompany().getCompanyId();
		this.companyName = request.getCompany().getCompanyName();
		this.approverId = request.getApprover() != null ? request.getApprover().getCandidate().getCandidateId() : null;
		this.status = request.getStatus().name();
	}

	public Long getId() {
		return id;
	}

	public Long getProjectId() {
		return projectId;
	}

	public String getRequestedBy() {
		return requestedBy;
	}

	public String getCompanyId() {
		return companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getApproverId() {
		return approverId;
	}

	public String getStatus() {
		return status;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setApproverId(String approverId) {
		this.approverId = approverId;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ProjectResponseDTO getProject() {
		return project;
	}

	public void setProject(ProjectResponseDTO project) {
		this.project = project;
	}

}
