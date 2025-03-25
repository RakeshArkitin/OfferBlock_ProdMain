package com.offerblock.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.offerblock.dto.CompanySignup;
import com.offerblock.entity.Company;
import com.offerblock.entity.Role;
import com.offerblock.enums.ERole;
import com.offerblock.exception.DuplicateValueExistsException;
import com.offerblock.exception.ResourceNotFoundException;
import com.offerblock.repository.CompanyRepository;
import com.offerblock.repository.RoleRepository;
import com.offerblock.service.CompanyService;
import jakarta.transaction.Transactional;

@Service
public class CompanyServiceImpl implements CompanyService {

	private final CompanyRepository companyRepository;

	private final RoleRepository roleRepository;

	private final PasswordEncoder encoder;

	@Autowired
	public CompanyServiceImpl(CompanyRepository companyRepository, RoleRepository roleRepository,
			PasswordEncoder encoder) {
		super();
		this.companyRepository = companyRepository;
		this.roleRepository = roleRepository;
		this.encoder = encoder;
	}

	@Transactional
	@Override
	public Company save(CompanySignup companySignup) {

		if (companyRepository.existsByEmail(companySignup.getEmail())) {
			throw new DuplicateValueExistsException("Error: Email is already in use");
		}

		String companyPrefix = "OBCO" + companySignup.getCompanyName().substring(0, 2).toUpperCase();
		long count = companyRepository.countCompaniesWithPrefix(companyPrefix);
		String companyId = String.format("%s%03d", companyPrefix, count + 1);

		System.out.println("Generated Company ID: " + companyId);

		Company company = new Company(companyId, encoder.encode(companySignup.getPassword()), companySignup.getEmail(),
				companySignup.getCompanyName(), companySignup.getCompanyAddress());

		Set<String> strRoles = companySignup.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null || strRoles.isEmpty()) {
			// Default role is COMPANY
			Role defaultRole = roleRepository.findByName(ERole.ROLE_COMPANY)
					.orElseThrow(() -> new RuntimeException("Error: Role not found"));
			roles.add(defaultRole);
		} else {
			for (String role : strRoles) {
				if (!role.equalsIgnoreCase("ROLE_COMPANY")) {
					throw new RuntimeException("Error: Invalid role '" + role + "' for Company");
				}
				Role companyRole = roleRepository.findByName(ERole.ROLE_COMPANY)
						.orElseThrow(() -> new RuntimeException("Error: Role not found"));
				roles.add(companyRole);
			}
		}

		company.setRoles(roles);

		return companyRepository.save(company);
	}

	@Transactional
	@Override
	public List<Company> getAllCandidate() {
		List<Company> companyAll = companyRepository.findAll();
		return companyAll;
	}

	@Transactional
	@Override
	public Company getCandidateById(Long companyId) {
		Company company = companyRepository.findById(companyId)
				.orElseThrow(() -> new ResourceNotFoundException("Candidate not found :" + companyId));
		return company;
	}

	@Transactional
	@Override
	public void deleteCandidate(Long companyId) {
		if (!companyRepository.existsById(companyId)) {
			throw new ResourceNotFoundException("Candidate not found :" + companyId);
		}
		companyRepository.deleteById(companyId);
	}
}
