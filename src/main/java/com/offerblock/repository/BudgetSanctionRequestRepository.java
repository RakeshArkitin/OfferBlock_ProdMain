package com.offerblock.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.offerblock.entity.BudgetSanctionRequest;
import com.offerblock.entity.BudgetSanctioner;
import com.offerblock.entity.Candidate;
import com.offerblock.entity.Company;
import com.offerblock.enums.BudgetStatus;

@Repository
public interface BudgetSanctionRequestRepository extends JpaRepository<BudgetSanctionRequest, Long> {

	List<BudgetSanctionRequest> findBySanctioner(BudgetSanctioner sanctioner);

	@Query("SELECT b FROM BudgetSanctionRequest b WHERE b.sanctioner.candidate = :sanctioner AND b.status = :status")
	List<BudgetSanctionRequest> findBySanctionerAndStatus(@Param("sanctioner") Candidate sanctioner,
	                                                      @Param("status") BudgetStatus status);

	@Query("SELECT b FROM BudgetSanctionRequest b WHERE b.company = :company AND b.status = :status")
	List<BudgetSanctionRequest> findByCompanyAndStatus(@Param("company") Company company,
	                                                   @Param("status") BudgetStatus status);

}
