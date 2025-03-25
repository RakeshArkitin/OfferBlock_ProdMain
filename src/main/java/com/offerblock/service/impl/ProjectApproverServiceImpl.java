package com.offerblock.service.impl;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.offerblock.dto.AssignApproverRequest;
import com.offerblock.entity.Candidate;
import com.offerblock.entity.Company;
import com.offerblock.entity.ProjectApprover;
import com.offerblock.entity.Role;
import com.offerblock.enums.ERole;
import com.offerblock.exception.DuplicateValueExistsException;
import com.offerblock.exception.ResourceNotFoundException;
import com.offerblock.repository.CandidateRepository;
import com.offerblock.repository.CompanyRepository;
import com.offerblock.repository.ProjectApproverRepository;
import com.offerblock.repository.RoleRepository;
import com.offerblock.service.ProjectApproverService;
import jakarta.transaction.Transactional;

@Service
public class ProjectApproverServiceImpl implements ProjectApproverService {

	private final CandidateRepository candidateRepository;
	private final RoleRepository roleRepository;
	private final CompanyRepository companyRepository;
	private final ProjectApproverRepository projectApproverRepository;

	@Autowired
	public ProjectApproverServiceImpl(CandidateRepository candidateRepository, RoleRepository roleRepository,
			CompanyRepository companyRepository, ProjectApproverRepository projectApproverRepository) {
		this.candidateRepository = candidateRepository;
		this.roleRepository = roleRepository;
		this.companyRepository = companyRepository;
		this.projectApproverRepository = projectApproverRepository;
	}

	@Override
	public ProjectApprover assignProjectApprover(AssignApproverRequest request, Principal principal) {

		String candidateId = request.getCandidateId();
		String designation = request.getDesignation();

		Candidate candidate = candidateRepository.findByCandidateId(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

		Optional<ProjectApprover> activeApprover = projectApproverRepository
				.findByCandidate_CandidateIdAndActiveTrue(candidateId);
		if (activeApprover.isPresent()) {
			throw new DuplicateValueExistsException(
					"Candidate is currently working as an approver in another company.");
		}

		String companyEmail = principal.getName();
		Company company = companyRepository.findByEmail(companyEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		Role approverRole = roleRepository.findByName(ERole.ROLE_APPROVER)
				.orElseThrow(() -> new RuntimeException("Approver role not found"));

		if (!candidate.hasApproverRole()) {
			candidate.getRoles().add(approverRole);
			candidateRepository.save(candidate);
		}

		ProjectApprover approver = new ProjectApprover();
		approver.setCandidate(candidate);
		approver.setName(candidate.getUsername());
		approver.setDesignation(designation);
		approver.setCompany(company);
		approver.setActive(true);

		return projectApproverRepository.save(approver);
	}

	@Transactional
	@Override
	public List<ProjectApprover> getAllApprover() {
		return projectApproverRepository.findAll();
	}

	@Transactional
	@Override
	public ProjectApprover updateProjectApprover(String candidateId, String designation, Principal principal) {
		Candidate candidate = candidateRepository.findByCandidateId(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

		ProjectApprover approver = projectApproverRepository.findByCandidate(candidate)
				.orElseThrow(() -> new ResourceNotFoundException("Approver not found for this candidate"));

		Company company = companyRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		if (!approver.getCompany().getId().equals(company.getId())) {
			throw new RuntimeException("Unauthorized to update this approver");
		}

		approver.setDesignation(designation);
		return projectApproverRepository.save(approver);
	}

	@Override
	public void deleteProjectApprover(String candidateId) {
		ProjectApprover approver = projectApproverRepository.findByCandidate_CandidateId(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("Approver not found with ID: " + candidateId));

		Candidate candidate = approver.getCandidate();

		Role approverRole = roleRepository.findByName(ERole.ROLE_APPROVER)
				.orElseThrow(() -> new RuntimeException("Approver role not found"));

		if (candidate.getRoles().contains(approverRole)) {
			candidate.getRoles().remove(approverRole);
			candidateRepository.save(candidate);
		}

		approver.setActive(false);
		projectApproverRepository.delete(approver);
	}

	@Override
	public ProjectApprover getProjectApproverByCandidateIdForCompany(String candidateId, Principal principal) {
		String email = principal.getName();
		Company loggedInCompany = companyRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		return projectApproverRepository.findByCandidate_CandidateIdAndCompany_Id(candidateId, loggedInCompany.getId())
				.orElseThrow(
						() -> new ResourceNotFoundException("Approver not found for this candidate in your company"));
	}

	@Override
	public void deactiveProjectApprover(String candidateId, Principal principal) {
		Candidate candidate = candidateRepository.findByCandidateId(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

		Company company = companyRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		ProjectApprover approver = projectApproverRepository
				.findByCandidate_CandidateIdAndCompany_Id(candidateId, company.getId()).orElseThrow(
						() -> new ResourceNotFoundException("Approver not found for this candidate in your company"));

		approver.setActive(false);
		projectApproverRepository.save(approver);
	}

	@Override
	public List<ProjectApprover> getProjectApproverHistoryByCandidateId(String candidateId) {
		Candidate candidate = candidateRepository.findByCandidateId(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

		return projectApproverRepository.findAllByCandidate_CandidateId(candidateId);
	}

}
