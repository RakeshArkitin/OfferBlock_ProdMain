package com.offerblock.service.impl;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.offerblock.dto.AssignRecruiterRequest;
import com.offerblock.entity.Candidate;
import com.offerblock.entity.Company;
import com.offerblock.entity.Recruiter;
import com.offerblock.entity.Role;
import com.offerblock.enums.ERole;
import com.offerblock.exception.DuplicateValueExistsException;
import com.offerblock.exception.ResourceNotFoundException;
import com.offerblock.repository.CandidateRepository;
import com.offerblock.repository.CompanyRepository;
import com.offerblock.repository.RecruiterRepository;
import com.offerblock.repository.RoleRepository;
import com.offerblock.service.RecruiterService;
import jakarta.transaction.Transactional;

@Service
public class RecruiterServiceImpl implements RecruiterService {

	private final CandidateRepository candidateRepository;
	private final CompanyRepository companyRepository;
	private final RoleRepository roleRepository;
	private final RecruiterRepository recruiterRepository;

	@Autowired
	public RecruiterServiceImpl(CandidateRepository candidateRepository, CompanyRepository companyRepository,
			RoleRepository roleRepository, RecruiterRepository recruiterRepository) {
		super();
		this.candidateRepository = candidateRepository;
		this.companyRepository = companyRepository;
		this.roleRepository = roleRepository;
		this.recruiterRepository = recruiterRepository;
	}

	@Override
	public Recruiter assignCandidateAsRecruiter(AssignRecruiterRequest request, Principal principal) {

		String candidateId = request.getCandidateId();
		String designation = request.getDesignation();

		Candidate candidate = candidateRepository.findByCandidateId(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

		Optional<Recruiter> activeRecruiter = recruiterRepository.findByCandidate_CandidateIdAndActiveTrue(candidateId);

		if (activeRecruiter.isPresent()) {
			throw new DuplicateValueExistsException("Candidate is currently working as recruiter in another company.");
		}

		String companyEmail = principal.getName();
		Company company = companyRepository.findByEmail(companyEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		Role recruiterRole = roleRepository.findByName(ERole.ROLE_RECRUITER)
				.orElseThrow(() -> new RuntimeException("Recruiter role not found"));

		if (!candidate.hasRecruiterRole()) {
			candidate.getRoles().add(recruiterRole);
			candidateRepository.save(candidate);
		}

		Recruiter recruiter = new Recruiter();
		recruiter.setCandidate(candidate);
		recruiter.setName(candidate.getUsername());
		recruiter.setDesignation(designation);
		recruiter.setCompany(company);
		recruiter.setActive(true);

		return recruiterRepository.save(recruiter);
	}

	@Transactional
	@Override
	public List<Recruiter> getAllRecruiters() {
		List<Recruiter> recruiterAll = recruiterRepository.findAll();
		return recruiterAll;
	}

	@Transactional
	@Override
	public Recruiter updateRecruiter(String candidateId, String designation, Principal principal) {

		Candidate candidate = candidateRepository.findByCandidateId(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

		Recruiter recruiter = recruiterRepository.findByCandidate(candidate)
				.orElseThrow(() -> new ResourceNotFoundException("Recruiter not found for this candidate"));

		Company company = companyRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		if (!recruiter.getCompany().getId().equals(company.getId())) {
			throw new RuntimeException("Unauthorized to update this recruiter");
		}

		recruiter.setDesignation(designation);
		return recruiterRepository.save(recruiter);
	}

	@Override
	public void deleteRecruiter(String candidateId) {

		Recruiter recruiter = recruiterRepository.findByCandidate_CandidateId(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with ID: " + candidateId));

		Candidate candidate = recruiter.getCandidate();

		Role recruiterRole = roleRepository.findByName(ERole.ROLE_RECRUITER)
				.orElseThrow(() -> new RuntimeException("Recruiter role not found"));

		if (candidate.getRoles().contains(recruiterRole)) {
			candidate.getRoles().remove(recruiterRole);
			candidateRepository.save(candidate);
		}

		recruiter.setActive(false);
		recruiterRepository.delete(recruiter);
	}

	@Override
	public Recruiter getRecruiterByCandidateIdForCompany(String candidateId, Principal principal) {
		// Get the currently logged-in company via token
		String email = principal.getName();
		Company loggedInCompany = companyRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		// Fetch recruiter by candidateId + companyId
		return recruiterRepository.findByCandidate_CandidateIdAndCompany_Id(candidateId, loggedInCompany.getId())
				.orElseThrow(
						() -> new ResourceNotFoundException("Recruiter not found for this candidate in your company"));
	}

	@Override
	public void deactiveRecruiter(String candidateId, Principal principal) {
		candidateRepository.findByCandidateId(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

		Company company = companyRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		Recruiter recruiter = recruiterRepository.findByCandidate_CandidateIdAndCompany_Id(candidateId, company.getId())
				.orElseThrow(
						() -> new ResourceNotFoundException("Recruiter not found for this candidate in your company"));

		recruiter.setActive(false);
		recruiterRepository.save(recruiter);
	}
	
	public List<Recruiter> getRecruiterHistoryByCandidateId(String candidateId) {
	    candidateRepository.findByCandidateId(candidateId)
	            .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

	    return recruiterRepository.findAllByCandidate_CandidateId(candidateId);
	}


}
