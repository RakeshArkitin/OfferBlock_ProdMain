package com.offerblock.service;

import java.security.Principal;
import java.util.List;

import com.offerblock.dto.AssignApproverRequest;
import com.offerblock.entity.ProjectApprover;

public interface ProjectApproverService {

	public ProjectApprover assignProjectApprover(AssignApproverRequest request, Principal principal);

	public ProjectApprover updateProjectApprover(String candidateId, String designation, Principal principal);

	public void deleteProjectApprover(String candidateId);

	ProjectApprover getProjectApproverByCandidateIdForCompany(String candidateId, Principal principal);

	void deactiveProjectApprover(String candidateId, Principal principal);

	List<ProjectApprover> getProjectApproverHistoryByCandidateId(String candidateId);

}
