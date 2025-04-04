package com.offerblock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.offerblock.dto.CompanySignup;
import com.offerblock.repository.CompanyRepository;
import com.offerblock.service.CompanyService;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
@RequestMapping("/api/auth")
public class CompanyController {

	private final CompanyService companyService;

	private final CompanyRepository companyRepository;

	@Autowired
	public CompanyController(CompanyService companyService, CompanyRepository companyRepository) {
		super();
		this.companyService = companyService;
		this.companyRepository = companyRepository;
	}

	@PostMapping("/companysignup")
	public ResponseEntity<String> save(@Valid @RequestBody CompanySignup companySignup) {
		String message = "";
		try {
			if (companyRepository.existsByEmail(companySignup.getEmail())) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(
						"The email '" + companySignup.getEmail() + "' is already registered. Try logging in instead.");
			}
			message = "Company register successfully";
			companyService.save(companySignup);
			return new ResponseEntity<>(message, HttpStatus.CREATED);

		} catch (Exception e) {
			message = "Invalid Data";
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}

	}

	@PreAuthorize("hasRole('COMPANY')")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		String message = "";
		try {
			companyRepository.deleteById(id);
			message = "Successfully Deleted";
			return new ResponseEntity<>(message, HttpStatus.OK);
		} catch (Exception e) {
			message = "Details Invalid";
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
	}

}
