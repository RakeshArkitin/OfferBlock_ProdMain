package com.offerblock.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.offerblock.dto.AssignRecruiterRequest;
import com.offerblock.entity.Company;
import com.offerblock.entity.Recruiter;
import com.offerblock.repository.CompanyRepository;
import com.offerblock.repository.RecruiterRepository;
import com.offerblock.service.RecruiterService;

@RequestMapping("/api/recruiter")
@RestController
@CrossOrigin("*")
public class RecruiterController {

	private final RecruiterService recruiterService;
	private final RecruiterRepository recruiterRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	public RecruiterController(RecruiterService recruiterService, RecruiterRepository recruiterRepository) {
		super();
		this.recruiterService = recruiterService;
		this.recruiterRepository = recruiterRepository;
	}

	@PostMapping("/assign")
	@PreAuthorize("hasRole('COMPANY')")
	public ResponseEntity<?> assignRecruiter(@RequestBody AssignRecruiterRequest request, Principal principal) {
		String message = "";
		try {
			recruiterService.assignCandidateAsRecruiter(request, principal);
			message = "Recruiter assigned successfully...";
			return new ResponseEntity<>(message, HttpStatus.OK);
		} catch (Exception e) {
			message = "Details Invalid";
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasRole('COMPANY')")
	@PutMapping("/update/{candidateId}")
	public ResponseEntity<?> updateRecruiter(@PathVariable String candidateId,
			@RequestParam("designation") String designation, Principal principal) {
		recruiterService.updateRecruiter(candidateId, designation, principal);
		return ResponseEntity.ok("Recruiter updated successfully.");
	}

	@PreAuthorize("hasRole('COMPANY')")
	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteRecruiter(@RequestParam("candidateId") String candidateId) {
		recruiterService.deleteRecruiter(candidateId);
		return ResponseEntity.ok("Recruiter deleted successfully.");
	}

	@GetMapping("/get/{candidateId}")
	@PreAuthorize("hasRole('COMPANY')")
	public ResponseEntity<Recruiter> getRecruiterByCandidateId(@PathVariable String candidateId, Principal principal) {
		Recruiter recruiter = recruiterService.getRecruiterByCandidateIdForCompany(candidateId, principal);
		return ResponseEntity.ok(recruiter);
	}

	@PreAuthorize("hasRole('COMPANY')")
	@PutMapping("/deactivate/{candidateId}")
	public ResponseEntity<?> deactiveRecruiter(@PathVariable String candidateId, Principal principal) {
		recruiterService.deactiveRecruiter(candidateId, principal);
		return ResponseEntity.ok("Recruiter deactivated successfully..");
	}

	@GetMapping("/history/{candidateId}")
	@PreAuthorize("hasAnyRole('COMPANY', 'SUPER_ADMIN')")
	public ResponseEntity<List<Recruiter>> getRecruiterHistory(@PathVariable String candidateId) {
		List<Recruiter> history = recruiterService.getRecruiterHistoryByCandidateId(candidateId);
		return ResponseEntity.ok(history);
	}

	@PreAuthorize("hasRole('COMPANY')")
	@GetMapping("/getAll")
	public ResponseEntity<List<Recruiter>> getActiveRecruitersByCompany(Principal principal) {
		String companyEmail = principal.getName(); // from JWT
		Company company = companyRepository.findByEmail(companyEmail)
				.orElseThrow(() -> new RuntimeException("Company not found"));

		List<Recruiter> recruiters = recruiterRepository.findByCompanyIdAndActiveTrue(company.getId());

		return ResponseEntity.ok(recruiters);
	}

}
