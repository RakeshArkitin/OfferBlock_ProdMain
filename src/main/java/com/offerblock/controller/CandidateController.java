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
        candidateService.save(candidateSignup);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Success: Candidate '" + candidateSignup.getUsername() + "' registered successfully!");
    }

    @PreAuthorize("hasRole('CANDIDATE')")
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
        candidateService.deleteById(candidateId);
        return ResponseEntity.ok("Candidate deleted successfully");
    }

    @PreAuthorize("hasRole('COMPANY')")
    @PostMapping("/assign-recruiter/{candidateId}")
    public ResponseEntity<String> assignRecruiterRole(@PathVariable String candidateId) {
        candidateService.assignRecruiterRole(candidateId);
        return ResponseEntity.ok("Candidate assigned as recruiter successfully.");
    }
}
