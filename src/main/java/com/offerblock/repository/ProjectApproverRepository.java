package com.offerblock.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.offerblock.entity.Candidate;
import com.offerblock.entity.ProjectApprover;

public interface ProjectApproverRepository extends JpaRepository<ProjectApprover, Long> {

	Optional<ProjectApprover> findByCandidate(Candidate candidate);

	Optional<ProjectApprover> findByCandidate_CandidateId(String candidateId);

	Optional<ProjectApprover> findByCandidate_CandidateIdAndCompany_Id(String candidateId, Long companyId);

	Optional<ProjectApprover> findByCandidate_CandidateIdAndActiveTrue(String candidateId);

	List<ProjectApprover> findByCompany_Id(Long id);

	List<ProjectApprover> findAllByCandidate_CandidateId(String candidateId);

	List<ProjectApprover> findByCompanyIdAndActiveTrue(Long companyId);
}
