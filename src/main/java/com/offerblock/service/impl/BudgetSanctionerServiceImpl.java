package com.offerblock.service.impl;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.offerblock.dto.AssignSanctionerRequest;
import com.offerblock.entity.BudgetSanctioner;
import com.offerblock.entity.Candidate;
import com.offerblock.entity.Company;
import com.offerblock.entity.Role;
import com.offerblock.enums.ERole;
import com.offerblock.exception.DuplicateValueExistsException;
import com.offerblock.exception.ResourceNotFoundException;
import com.offerblock.repository.BudgetSanctionerRepository;
import com.offerblock.repository.CandidateRepository;
import com.offerblock.repository.CompanyRepository;
import com.offerblock.repository.RoleRepository;
import com.offerblock.service.BudgetSanctionerService;
import jakarta.transaction.Transactional;

@Service
public class BudgetSanctionerServiceImpl implements BudgetSanctionerService {

	private final CandidateRepository candidateRepository;
	private final RoleRepository roleRepository;
	private final CompanyRepository companyRepository;
	private final BudgetSanctionerRepository budgetSanctionerRepository;

	@Autowired
	public BudgetSanctionerServiceImpl(CandidateRepository candidateRepository, RoleRepository roleRepository,
			CompanyRepository companyRepository, BudgetSanctionerRepository budgetSanctionerRepository) {
		super();
		this.candidateRepository = candidateRepository;
		this.roleRepository = roleRepository;
		this.companyRepository = companyRepository;
		this.budgetSanctionerRepository = budgetSanctionerRepository;
	}

	@Override
	public BudgetSanctioner assignBudgetApprover(AssignSanctionerRequest request, Principal principal) {

		String candidateId = request.getCandidateId();
		String designation = request.getDesignation();

		Candidate candidate = candidateRepository.findByCandidateId(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

		Optional<BudgetSanctioner> activeSanctioner = budgetSanctionerRepository
				.findByCandidate_CandidateIdAndActiveTrue(candidateId);

		if (activeSanctioner.isPresent()) {
			throw new DuplicateValueExistsException(
					"Candidate is currently working as a budget approver in another company.");
		}

		String companyEmail = principal.getName();
		Company company = companyRepository.findByEmail(companyEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		Role sanctionerRole = roleRepository.findByName(ERole.ROLE_SANCTIONER)
				.orElseThrow(() -> new RuntimeException("Sanctioner role not found"));

		if (!candidate.hasSanctionerRole()) {
			candidate.getRoles().add(sanctionerRole);
			candidateRepository.save(candidate);
		}

		BudgetSanctioner sanctioner = new BudgetSanctioner();
		sanctioner.setCandidate(candidate);
		sanctioner.setName(candidate.getUsername());
		sanctioner.setDesignation(designation);
		sanctioner.setCompany(company);
		sanctioner.setActive(true);

		return budgetSanctionerRepository.save(sanctioner);
	}

	@Transactional
	@Override
	public List<BudgetSanctioner> getAllBudgetApprovers() {
		return budgetSanctionerRepository.findAll();
	}

	@Transactional
	@Override
	public BudgetSanctioner updateBudget(String candidateId, String designation, Principal principal) {

		Candidate candidate = candidateRepository.findByCandidateId(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

		BudgetSanctioner sanctioner = budgetSanctionerRepository.findByCandidate(candidate)
				.orElseThrow(() -> new ResourceNotFoundException("Sanctioner not found"));

		Company company = companyRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		if (!sanctioner.getCompany().getId().equals(company.getId())) {
			throw new RuntimeException("Unauthorized to update this sanctioner");
		}

		sanctioner.setDesignation(designation);
		return budgetSanctionerRepository.save(sanctioner);
	}

	@Override
	public void deleteBudgetApprover(String candidateId) {

		Candidate candidate = candidateRepository.findByCandidateId(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

		BudgetSanctioner sanctioner = budgetSanctionerRepository.findByCandidate(candidate)
				.orElseThrow(() -> new ResourceNotFoundException("Sanctioner not found"));

		Role sanctionerRole = roleRepository.findByName(ERole.ROLE_SANCTIONER)
				.orElseThrow(() -> new RuntimeException("Sanctioner role not found"));

		if (candidate.getRoles().contains(sanctionerRole)) {
			candidate.getRoles().remove(sanctionerRole);
			candidateRepository.save(candidate);
		}

		sanctioner.setActive(false);
		budgetSanctionerRepository.delete(sanctioner);
	}

	@Override
	public BudgetSanctioner getBudgetSanctionerByCandidateIdForCompany(String candidateId, Principal principal) {

		String email = principal.getName();
		Company company = companyRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		return budgetSanctionerRepository.findByCandidate_CandidateIdAndCompany_Id(candidateId, company.getId())
				.orElseThrow(
						() -> new ResourceNotFoundException("Sanctioner not found for this candidate in your company"));
	}

	@Override
	public void deactiveBudgetSanctioner(String candidateId, Principal principal) {

		Candidate candidate = candidateRepository.findByCandidateId(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

		Company company = companyRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		BudgetSanctioner sanctioner = budgetSanctionerRepository
				.findByCandidate_CandidateIdAndCompany_Id(candidateId, company.getId()).orElseThrow(
						() -> new ResourceNotFoundException("Sanctioner not found for this candidate in your company"));

		sanctioner.setActive(false);
		budgetSanctionerRepository.save(sanctioner);
	}

	@Override
	public List<BudgetSanctioner> getBudgetSanctionerHistoryByCandidateId(String candidateId) {

		Candidate candidate = candidateRepository.findByCandidateId(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

		return budgetSanctionerRepository.findAllByCandidate_CandidateId(candidateId);
	}

}
