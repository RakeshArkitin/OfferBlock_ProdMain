package com.offerblock.service;

import java.util.List;
import com.offerblock.dto.CompanySignup;
import com.offerblock.entity.Company;

public interface CompanyService {

	public Company save(CompanySignup companySignup);

	public List<Company> getAllCandidate();

	public Company getCandidateById(Long companyId);

	public void deleteCandidate(Long companyId);
}
