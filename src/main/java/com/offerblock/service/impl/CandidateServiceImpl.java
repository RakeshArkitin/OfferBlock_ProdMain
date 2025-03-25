package com.offerblock.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.offerblock.dto.CandidateSignup;
import com.offerblock.entity.Candidate;
import com.offerblock.entity.Role;
import com.offerblock.enums.ERole;
import com.offerblock.exception.DuplicateValueExistsException;
import com.offerblock.exception.ResourceNotFoundException;
import com.offerblock.repository.CandidateRepository;
import com.offerblock.repository.RoleRepository;
import com.offerblock.service.CandidateService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class CandidateServiceImpl implements CandidateService {

	private static final String ERROR = "ERROR";

	private final CandidateRepository candidateRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public CandidateServiceImpl(CandidateRepository candidateRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder) {
		this.candidateRepository = candidateRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	@Override
	public Candidate save(@Valid CandidateSignup candidateSignup) {
		if (candidateRepository.existsByEmail(candidateSignup.getEmail())) {
			throw new DuplicateValueExistsException(ERROR + ": THE EMAIL '" + candidateSignup.getEmail()
					+ "' is already registered. Try logging in instead.");
		}

		if (candidateRepository.existsByPanCard(candidateSignup.getPanCard())) {
			throw new DuplicateValueExistsException(ERROR + ": THE PAN CARD '" + candidateSignup.getPanCard()
					+ "' IS ALREADY LINKED TO ANOTHER ACCOUNT.");
		}

		long count = candidateRepository.count() + 1;
		String candidateId = String.format("OBCM%03d", count);

		Candidate candidate = new Candidate(candidateId, candidateSignup.getUsername(), candidateSignup.getEmail(),
				passwordEncoder.encode(candidateSignup.getPassword()), candidateSignup.getPanCard());

		Set<String> strRoles = candidateSignup.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null || strRoles.isEmpty()) {
			// Default role is CANDIDATE
			Role defaultRole = roleRepository.findByName(ERole.ROLE_CANDIDATE)
					.orElseThrow(() -> new RuntimeException(ERROR + ": Role not found"));
			roles.add(defaultRole);
		} else {
			for (String role : strRoles) {
				switch (role.toUpperCase()) {
				case "ROLE_CANDIDATE":
					roles.add(roleRepository.findByName(ERole.ROLE_CANDIDATE)
							.orElseThrow(() -> new RuntimeException(ERROR + ": Role not found")));
					break;
				case "ROLE_RECRUITER":
					roles.add(roleRepository.findByName(ERole.ROLE_RECRUITER)
							.orElseThrow(() -> new RuntimeException(ERROR + ": Role not found")));
					break;
				default:
					throw new RuntimeException("Error: Invalid role '" + role + "'");
				}
			}
		}

		candidate.setRoles(roles);
		return candidateRepository.save(candidate);
	}

	@Transactional
	@Override
	public List<Candidate> getAllCandidates() {
		return candidateRepository.findAll();
	}

	@Transactional
	@Override
	public Candidate getCandidateById(String candidateId) {
		return candidateRepository.findByCandidateId(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate not found with ID: " + candidateId));
	}

	@Transactional
	@Override
	public void deleteById(String candidateId) {
		Candidate candidate = getCandidateById(candidateId);
		candidateRepository.delete(candidate);
	}

	@Transactional
	@Override
	public void assignRecruiterRole(String candidateId) {
		Candidate candidate = getCandidateById(candidateId);
		if (!candidate.hasRecruiterRole()) {
			Role recruiterRole = roleRepository.findByName(ERole.ROLE_RECRUITER)
					.orElseThrow(() -> new RuntimeException(ERROR + ": Role not found"));
			candidate.getRoles().add(recruiterRole);
			candidateRepository.save(candidate);
		}
	}
}
