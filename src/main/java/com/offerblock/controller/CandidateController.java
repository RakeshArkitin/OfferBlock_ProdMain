package com.offerblock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.offerblock.dto.CandidateSignup;
import com.offerblock.entity.Candidate;
import com.offerblock.service.CandidateService;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
@RequestMapping("/api/auth")
public class CandidateController {

	private final CandidateService candidateService;

	@Autowired
	public CandidateController(CandidateService candidateService) {
		this.candidateService = candidateService;
	}

	@PostMapping("/candidatesignup")
	public ResponseEntity<String> save(@Valid @RequestBody CandidateSignup candidateSignup) {
		String message = "";
		try {
			candidateService.save(candidateSignup);
			message = "Candidate registered successfully!";
			return new ResponseEntity<>(message, HttpStatus.CREATED);
		} catch (Exception e) {
			message = "Invalid Data";
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}

	}

	@PreAuthorize("hasAnyRole('CANDIDATE','COMPANY')")
	@GetMapping("/{candidateId}")
	public ResponseEntity<Object> getCandidate(@PathVariable String candidateId) {
		try {
			Candidate candidate = candidateService.getCandidateById(candidateId);
			return ResponseEntity.ok(candidate);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
		}
	}

	@PreAuthorize("hasRole('COMPANY')")
	@DeleteMapping("/{candidateId}")
	public ResponseEntity<String> delete(@PathVariable String candidateId) {

		String message = "";
		try {
			candidateService.deleteById(candidateId);
			message = "Candidate deleted successfully";
			return new ResponseEntity<>(message, HttpStatus.OK);
		} catch (Exception e) {
			message = "Details Invalid";
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}

	}

	@PreAuthorize("hasRole('COMPANY')")
	@PostMapping("/assign-recruiter/{candidateId}")
	public ResponseEntity<String> assignRecruiterRole(@PathVariable String candidateId) {
		String message = "";
		try {
			candidateService.assignRecruiterRole(candidateId);
			message = "Candidate assigned as recruiter successfully...";
			return new ResponseEntity<>(message, HttpStatus.OK);
		} catch (Exception e) {
			message = "Details Invalid";
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}

	}
}
