package com.offerblock.service.impl;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.offerblock.dto.LoginRequest;
import com.offerblock.entity.BudgetSanctioner;
import com.offerblock.entity.Candidate;
import com.offerblock.entity.Company;
import com.offerblock.entity.ProjectApprover;
import com.offerblock.entity.Recruiter;
import com.offerblock.repository.BudgetSanctionerRepository;
import com.offerblock.repository.CandidateRepository;
import com.offerblock.repository.CompanyRepository;
import com.offerblock.repository.ProjectApproverRepository;
import com.offerblock.repository.RecruiterRepository;
import com.offerblock.response.AuthResponse;
import com.offerblock.response.MessageResponse;
import com.offerblock.utils.JwtUtils;
import jakarta.validation.Valid;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
	private final CompanyRepository companyRepository;
	private final CandidateRepository candidateRepository;
	private final RecruiterRepository recruiterRepository;
	private final ProjectApproverRepository projectApproverRepository;
	private final BudgetSanctionerRepository budgetSanctionerRepository;

	@Autowired
	public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
			CompanyRepository companyRepository, CandidateRepository candidateRepository,
			RecruiterRepository recruiterRepository, ProjectApproverRepository projectApproverRepository,
			BudgetSanctionerRepository budgetSanctionerRepository) {
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
		this.companyRepository = companyRepository;
		this.candidateRepository = candidateRepository;
		this.recruiterRepository = recruiterRepository;
		this.projectApproverRepository = projectApproverRepository;
		this.budgetSanctionerRepository = budgetSanctionerRepository;
	}

	public ResponseEntity<?> authenticate(@Valid LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateToken(authentication);

			List<String> roles = new ArrayList<>();
			String userId = null;
			String companyName = null;

			Optional<Company> companyOpt = companyRepository.findByEmail(loginRequest.getEmail());
			if (companyOpt.isPresent()) {
				Company company = companyOpt.get();
				roles.add("COMPANY");
				userId = company.getCompanyId();
				companyName = company.getCompanyName();
				return ResponseEntity.ok(new AuthResponse(jwt, roles, userId, companyName));
			}

			Optional<Candidate> candidateOpt = candidateRepository.findByEmail(loginRequest.getEmail());
			if (candidateOpt.isPresent()) {
				Candidate candidate = candidateOpt.get();
				userId = candidate.getCandidateId();
				roles.add("CANDIDATE");

				Optional<Recruiter> recruiterOpt = recruiterRepository.findByCandidate_CandidateIdAndActiveTrue(userId);
				if (recruiterOpt.isPresent()) {
					roles.add("RECRUITER");
				}

				Optional<ProjectApprover> approverOpt = projectApproverRepository
						.findByCandidate_CandidateIdAndActiveTrue(userId);
				if (approverOpt.isPresent()) {
					roles.add("APPROVER");
				}
				
				Optional<BudgetSanctioner> sanctionerOpt= budgetSanctionerRepository.findByCandidate_CandidateIdAndActiveTrue(userId);
				if(sanctionerOpt.isPresent()) {
					roles.add("SANCTIONER");
				}

				return ResponseEntity.ok(new AuthResponse(jwt, roles, userId));
			}

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new MessageResponse("INVALID EMAIL OR PASSWORD"));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new MessageResponse("ERROR: INVALID USERNAME OR PASSWORD"));
		}
	}
}
