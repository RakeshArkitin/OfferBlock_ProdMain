package com.offerblock.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.offerblock.entity.Admin;
import com.offerblock.entity.Candidate;
import com.offerblock.entity.Company;
import com.offerblock.repository.AdminRepository;
import com.offerblock.repository.CandidateRepository;
import com.offerblock.repository.CompanyRepository;

import jakarta.transaction.Transactional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

	private final AdminRepository adminRepository;

	private final CandidateRepository candidateRepository;

	private final CompanyRepository companyRepository;

	@Autowired
	public UserDetailServiceImpl(AdminRepository adminRepository, CandidateRepository candidateRepository,
			CompanyRepository companyRepository) {
		super();
		this.adminRepository = adminRepository;
		this.candidateRepository = candidateRepository;
		this.companyRepository = companyRepository;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Admin admin = adminRepository.findByUsername(email).orElse(null);
		if (admin != null) {
			return UserDetailsImpl.build(admin);
		}

		Company company = companyRepository.findByEmail(email).orElse(null);
		if (company != null) {
			return UserDetailsImpl.build(company);
		}

		Candidate candidate = candidateRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));
		return UserDetailsImpl.build(candidate);
	}

}
