package com.offerblock.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.offerblock.entity.Candidate;
import com.offerblock.entity.Recruiter;

public interface RecruiterRepository extends JpaRepository<Recruiter, Long> {

	Optional<Recruiter> findByCandidate(Candidate candidate);

	Optional<Recruiter> findByCandidate_CandidateId(String candidateId);

	Optional<Recruiter> findByCandidate_CandidateIdAndCompany_Id(String candidateId, Long companyId);

    Optional<Recruiter> findByCandidate_CandidateIdAndActiveTrue(String candidateId);

	List<Recruiter> findByCompany_Id(Long id);
	
	List<Recruiter> findAllByCandidate_CandidateId(String candidateId);
	
	List<Recruiter> findByCompanyIdAndActiveTrue(Long companyId);
	
	



}
