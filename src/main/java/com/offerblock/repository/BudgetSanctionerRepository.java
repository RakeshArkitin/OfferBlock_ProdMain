package com.offerblock.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.offerblock.entity.BudgetSanctioner;
import com.offerblock.entity.Candidate;
import com.offerblock.entity.Company;

public interface BudgetSanctionerRepository extends JpaRepository<BudgetSanctioner, Long> {

    Optional<BudgetSanctioner> findByCandidate(Candidate candidate);

    Optional<BudgetSanctioner> findByCandidate_CandidateId(String candidateId);

    Optional<BudgetSanctioner> findByCandidate_CandidateIdAndCompany_Id(String candidateId, Long companyId);

    Optional<BudgetSanctioner> findByCandidate_CandidateIdAndActiveTrue(String candidateId);

    List<BudgetSanctioner> findByCompany_Id(Long companyId);

    List<BudgetSanctioner> findAllByCandidate_CandidateId(String candidateId);

    List<BudgetSanctioner> findByCompanyIdAndActiveTrue(Long companyId);

	List<BudgetSanctioner> findByCompany(Company company);
}
