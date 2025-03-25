package com.offerblock.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.offerblock.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {

	Optional<Company> findByEmail(String email);

	boolean existsByEmail(String email);

	@Query("SELECT COUNT(c) FROM Company c WHERE c.companyId LIKE :prefix%")
	long countCompaniesWithPrefix(@Param("prefix") String prefix);

	Optional<Company> findByCompanyId(String companyId);

}
