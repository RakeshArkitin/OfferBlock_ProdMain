package com.offerblock.service;

import java.security.Principal;
import java.util.List;

import com.offerblock.dto.AssignRecruiterRequest;
import com.offerblock.entity.Recruiter;

public interface RecruiterService {

	List<Recruiter> getAllRecruiters();

	Recruiter updateRecruiter(String candidateId, String designation, Principal principal);

	public void deleteRecruiter(String candidateId);

	Recruiter assignCandidateAsRecruiter(AssignRecruiterRequest request, Principal principal);

	Recruiter getRecruiterByCandidateIdForCompany(String candidateId, Principal principal);

	void deactiveRecruiter(String candidateId, Principal principal);

	List<Recruiter> getRecruiterHistoryByCandidateId(String candidateId);

}
