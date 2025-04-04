package com.offerblock.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.offerblock.entity.Offer;
import com.offerblock.exception.ResourceNotFoundException;
import com.offerblock.repository.OfferRepository;
import com.offerblock.service.impl.OfferServiceImpl;

@RestController
@RequestMapping("/api/offers")
@CrossOrigin(origins = "*")
public class OfferController {

	private final OfferServiceImpl offerService;

	private final OfferRepository offerRepository;

	@Autowired
	public OfferController(OfferServiceImpl offerService, OfferRepository offerRepository) {
		super();
		this.offerService = offerService;
		this.offerRepository = offerRepository;
	}

	@PreAuthorize("hasAnyRole('HR', 'COMPANY')")
	@PostMapping("/send")
	public ResponseEntity<Map<String, String>> sendOffer(@RequestParam String candidateId,
			@RequestParam String candidateName, @RequestParam String position, @RequestParam String ctc,
			@RequestParam String jobLocation, @RequestParam String deadline, @RequestParam LocalDate joiningDate,
			@RequestParam MultipartFile offerPdf, Principal principal) {

		try {
			offerService.createOffer(candidateId, candidateName, position, ctc, jobLocation, deadline, joiningDate,
					offerPdf, principal);

			Map<String, String> response = new HashMap<>();
			response.put("message", "Offer sent successfully!");
			return ResponseEntity.ok(response);

		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.body(Collections.singletonMap("error", "Error processing file: " + e.getMessage()));
		}
	}

	@PreAuthorize("hasAnyRole('HR', 'COMPANY')")
	@GetMapping("/getAll")
	public ResponseEntity<List<Map<String, Object>>> getAllOffers() {
		return ResponseEntity.ok(offerService.getAllOffers());
	}

	@PreAuthorize("hasRole('COMPANY')")
	@GetMapping("/getAlloffer")
	public ResponseEntity<List<Offer>> getAllOffers1() {
		return ResponseEntity.ok(offerRepository.findAll());
	}

	@PreAuthorize("hasRole('COMPANY')")
	@GetMapping("/{id}")
	public ResponseEntity<Offer> getOfferById(@PathVariable Long id) {
		Optional<Offer> offer = offerService.getOfferById(id);
		return offer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('COMPANY')")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteById(@PathVariable Long id) {
		String message = "";
		try {
			offerService.deleteByID(id);
			message = "Offer Deleted Successfully...";
			return new ResponseEntity<String>(message, HttpStatus.CREATED);
		} catch (Exception e) {
			message = "Details Invalid";
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}

	}

	@PreAuthorize("hasRole('CANDIDATE')")
	@GetMapping("/candidate/{candidateId}")
	public ResponseEntity<?> getOffer(@PathVariable String candidateId) {

		List<Map<String, Object>> offers = offerService.getAllOffers();
		return ResponseEntity.ok(offers);
	}

	@PreAuthorize("hasRole('COMPANY')")
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateOffer(@PathVariable Long id, @RequestParam(required = false) String candidateId,
			@RequestParam(required = false) String candidateName,
			@RequestParam(required = false) MultipartFile offerPdf, @RequestParam(required = false) String position,
			@RequestParam(required = false) String ctc, @RequestParam(required = false) String jobLocation,
			@RequestParam(required = false) String deadline, @RequestParam(required = false) String joiningDate)
			throws IOException {
		String message = "";
		try {
			LocalDate parsedDeadline = (deadline != null) ? LocalDate.parse(deadline) : null;
			LocalDate parsedJoiningDate = (joiningDate != null) ? LocalDate.parse(joiningDate) : null;

			offerService.updateOffer(id, candidateId, candidateName, offerPdf, position, ctc, jobLocation,
					parsedDeadline, parsedJoiningDate);
			message = "UPDATED SUCCESSFULLY!!";
			return new ResponseEntity<>(message, HttpStatus.OK);
		} catch (ResourceNotFoundException e) {
			message = "OFFER NOT FOUND";
			return new ResponseEntity<String>(message, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			message = "INVALD DATE FORMAT";
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
	}

}
