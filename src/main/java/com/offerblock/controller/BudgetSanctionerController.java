package com.offerblock.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.offerblock.dto.AssignSanctionerRequest;
import com.offerblock.entity.BudgetSanctioner;
import com.offerblock.entity.Company;
import com.offerblock.repository.BudgetSanctionerRepository;
import com.offerblock.repository.CompanyRepository;
import com.offerblock.service.BudgetSanctionerService;

@RequestMapping("/api/sanctioner")
@RestController
@CrossOrigin("*")
public class BudgetSanctionerController {

	private final BudgetSanctionerService budgetSanctionerService;
	private final BudgetSanctionerRepository budgetSanctionerRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	public BudgetSanctionerController(BudgetSanctionerService budgetSanctionerService,
			BudgetSanctionerRepository budgetSanctionerRepository) {
		this.budgetSanctionerService = budgetSanctionerService;
		this.budgetSanctionerRepository = budgetSanctionerRepository;
	}

	@PostMapping("/assign")
	@PreAuthorize("hasRole('COMPANY')")
	public ResponseEntity<?> assignSanctioner(@RequestBody AssignSanctionerRequest request, Principal principal) {
		budgetSanctionerService.assignBudgetApprover(request, principal);
		return ResponseEntity.ok("Sanctioner assigned successfully.");
	}

	@PutMapping("/update/{candidateId}")
	@PreAuthorize("hasRole('COMPANY')")
	public ResponseEntity<?> updateSanctioner(@PathVariable String candidateId,
			@RequestParam("designation") String designation, Principal principal) {
		budgetSanctionerService.updateBudget(candidateId, designation, principal);
		return ResponseEntity.ok("Sanctioner updated successfully.");
	}

	@DeleteMapping("/delete")
	@PreAuthorize("hasRole('COMPANY')")
	public ResponseEntity<?> deleteSanctioner(@RequestParam("candidateId") String candidateId) {
		budgetSanctionerService.deleteBudgetApprover(candidateId);
		return ResponseEntity.ok("Sanctioner deleted successfully.");
	}

	@GetMapping("/get/{candidateId}")
	@PreAuthorize("hasRole('COMPANY')")
	public ResponseEntity<BudgetSanctioner> getSanctionerByCandidateId(@PathVariable String candidateId,
			Principal principal) {
		BudgetSanctioner sanctioner = budgetSanctionerService.getBudgetSanctionerByCandidateIdForCompany(candidateId,
				principal);
		return ResponseEntity.ok(sanctioner);
	}

	@PutMapping("/deactivate/{candidateId}")
	@PreAuthorize("hasRole('COMPANY')")
	public ResponseEntity<?> deactivateSanctioner(@PathVariable String candidateId, Principal principal) {
		budgetSanctionerService.deactiveBudgetSanctioner(candidateId, principal);
		return ResponseEntity.ok("Sanctioner deactivated successfully.");
	}

	@GetMapping("/history/{candidateId}")
	@PreAuthorize("hasAnyRole('COMPANY', 'SUPER_ADMIN')")
	public ResponseEntity<List<BudgetSanctioner>> getSanctionerHistory(@PathVariable String candidateId) {
		List<BudgetSanctioner> history = budgetSanctionerService.getBudgetSanctionerHistoryByCandidateId(candidateId);
		return ResponseEntity.ok(history);
	}

	@GetMapping("/getAll")
	@PreAuthorize("hasRole('COMPANY')")
	public ResponseEntity<List<BudgetSanctioner>> getActiveSanctionersByCompany(Principal principal) {
		String companyEmail = principal.getName();
		Company company = companyRepository.findByEmail(companyEmail)
				.orElseThrow(() -> new RuntimeException("Company not found"));

		List<BudgetSanctioner> sanctioners = budgetSanctionerRepository.findByCompanyIdAndActiveTrue(company.getId());
		return ResponseEntity.ok(sanctioners);
	}
}
