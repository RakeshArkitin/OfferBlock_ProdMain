package com.offerblock.service.impl;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.offerblock.dto.LoginRequest;
import com.offerblock.entity.Candidate;
import com.offerblock.entity.Company;
import com.offerblock.repository.CandidateRepository;
import com.offerblock.repository.CompanyRepository;
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


	@Autowired
	public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
			CompanyRepository companyRepository, CandidateRepository candidateRepository) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
		this.companyRepository = companyRepository;
		this.candidateRepository = candidateRepository;
	}

	public ResponseEntity<?> authenticate(@Valid LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateToken(authentication);

			Optional<Company> companyOpt = companyRepository.findByEmail(loginRequest.getEmail());
			if (companyOpt.isPresent()) {
				Company company = companyOpt.get();
				return ResponseEntity
						.ok(new AuthResponse(jwt, "COMPANY", company.getCompanyId(), company.getCompanyName()));
			}

			Optional<Candidate> candidateOpt = candidateRepository.findByEmail(loginRequest.getEmail());
			if (candidateOpt.isPresent()) {
				return ResponseEntity.ok(new AuthResponse(jwt, "CANDIDATE", candidateOpt.get().getCandidateId()));
			}

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new MessageResponse("INVALID EMAIL OR PASSWORD"));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new MessageResponse("ERROR: INVALID USERNAME OR PASSWORD"));
		}
	}
}
