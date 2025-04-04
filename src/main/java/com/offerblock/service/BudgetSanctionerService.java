package com.offerblock.service;

import java.security.Principal;
import java.util.List;
import com.offerblock.dto.AssignSanctionerRequest;
import com.offerblock.entity.BudgetSanctioner;

public interface BudgetSanctionerService {

	public BudgetSanctioner assignBudgetApprover(AssignSanctionerRequest request, Principal principal);

	public List<BudgetSanctioner> getAllBudgetApprovers();

	BudgetSanctioner updateBudget(String candidateId, String designation, Principal principal);

	List<BudgetSanctioner> getBudgetSanctionerHistoryByCandidateId(String candidateId);

	void deactiveBudgetSanctioner(String candidateId, Principal principal);

	BudgetSanctioner getBudgetSanctionerByCandidateIdForCompany(String candidateId, Principal principal);

	void deleteBudgetApprover(String candidateId);

}
