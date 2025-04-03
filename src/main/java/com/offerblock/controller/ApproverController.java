package com.offerblock.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.offerblock.dto.AssignApproverRequest;
import com.offerblock.entity.ProjectApprover;
import com.offerblock.service.ProjectApproverService;

@RequestMapping("/api/approver")
@RestController
@CrossOrigin("*")
public class ApproverController {

	private final ProjectApproverService projectApproverService;

	@Autowired
	public ApproverController(ProjectApproverService projectApproverService) {
		this.projectApproverService = projectApproverService;
	}

	@PreAuthorize("hasRole('COMPANY')")
	@PostMapping("/assign")
	public ResponseEntity<?> assignCandidateAsApprover(@RequestBody AssignApproverRequest request,
			Principal principal) {
		projectApproverService.assignProjectApprover(request, principal);
		return ResponseEntity.ok("Approver assigned successfully...");
	}

	@PreAuthorize("hasRole('COMPANY')")
	@PutMapping("/update/{candidateId}")
	public ResponseEntity<?> updateProjectApprover(@PathVariable String candidateId,
			@RequestParam("designation") String designation, Principal principal) {
		projectApproverService.updateProjectApprover(candidateId, designation, principal);
		return ResponseEntity.ok("Approver updated successfully.");
	}

	@PreAuthorize("hasRole('COMPANY')")
	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteProjectApprover(@RequestParam("candidateId") String candidateId) {
		projectApproverService.deleteProjectApprover(candidateId);
		return ResponseEntity.ok("Approver deleted and role removed successfully.");
	}

	@GetMapping("/get/{candidateId}")
	@PreAuthorize("hasRole('COMPANY')")
	public ResponseEntity<ProjectApprover> getApproverByCandidateId(@PathVariable String candidateId,
			Principal principal) {
		ProjectApprover approver = projectApproverService.getProjectApproverByCandidateIdForCompany(candidateId,
				principal);
		return ResponseEntity.ok(approver);
	}

	@PreAuthorize("hasRole('COMPANY')")
	@PutMapping("/deactivate/{candidateId}")
	public ResponseEntity<?> deactivateProjectApprover(@PathVariable String candidateId, Principal principal) {
		projectApproverService.deactiveProjectApprover(candidateId, principal);
		return ResponseEntity.ok("Approver deactivated successfully.");
	}

//	@GetMapping("/history/{candidateId}")
//	@PreAuthorize("hasAnyRole('COMPANY', 'SUPER_ADMIN')")
//	public ResponseEntity<List<ProjectApprover>> getApproverHistory(@PathVariable String candidateId) {
//		List<ProjectApprover> history = projectApproverService.getProjectApproverHistoryByCandidateId(candidateId);
//		return ResponseEntity.ok(history);
//	}

}
