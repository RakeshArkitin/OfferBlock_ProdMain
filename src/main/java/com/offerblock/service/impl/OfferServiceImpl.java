package com.offerblock.service.impl;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.offerblock.entity.Candidate;
import com.offerblock.entity.Company;
import com.offerblock.entity.Offer;
import com.offerblock.exception.ResourceNotFoundException;
import com.offerblock.repository.CandidateRepository;
import com.offerblock.repository.CompanyRepository;
import com.offerblock.repository.OfferRepository;
import com.offerblock.service.OfferService;

import jakarta.transaction.Transactional;

@Service
public class OfferServiceImpl implements OfferService {

//	private static final Logger logger = LoggerFactory.getLogger(OfferServiceImpl.class);

	private final OfferRepository offerRepository;
	private final CandidateRepository candidateRepository;

	private final CompanyRepository companyRepository;

	@Autowired
	public OfferServiceImpl(OfferRepository offerRepository, CandidateRepository candidateRepository,
			CompanyRepository companyRepository) {
		super();
		this.offerRepository = offerRepository;
		this.candidateRepository = candidateRepository;
		this.companyRepository = companyRepository;
	}

	@Transactional
	@Override
	public Offer createOffer(String candidateId, String candidateName, String position, String ctc, String jobLocation,
			String deadline, LocalDate joiningDate, MultipartFile offerPdf, Principal principal) throws IOException {

//		if (projectName == null || projectName.trim().isEmpty()) {
//			throw new IllegalArgumentException("Project name is required.");
//		}

		String userEmail = principal.getName();
		Optional<Company> companyOpt = companyRepository.findByEmail(userEmail);

		Company issuerCompany = null;
		if (companyOpt.isPresent()) {
			issuerCompany = companyOpt.get();
		} else {
			throw new IllegalArgumentException("User is not associated with a recruiter or company.");
		}

		Optional<Candidate> candidateOpt = candidateRepository.findByCandidateId(candidateId);
		if (!candidateOpt.isPresent()) {
			throw new IllegalArgumentException("Candidate with ID " + candidateId + " does not exist.");
		}

		Candidate candidate = candidateOpt.get();

		if (!candidate.getUsername().equalsIgnoreCase(candidateName)) {
			throw new IllegalArgumentException(
					"Candidate ID and Name do not match. Expected Name: " + candidate.getUsername());
		}

		Optional<Offer> offerOpt = offerRepository.findByCandidateAndPositionAndCtc(candidate, position, ctc);
		if (offerOpt.isPresent()) {
			throw new IllegalArgumentException("Duplicate offer already exists for this candidate.");
		}

		if (offerPdf.isEmpty()) {
			throw new IllegalArgumentException("Uploaded offer letter PDF is empty.");
		}

		LocalDate today = LocalDate.now();

		LocalDate parsedDealine = LocalDate.parse(deadline);
		if (parsedDealine.isBefore(today)) {
			throw new IllegalArgumentException("Deadline date cannot be in past.");
		}

		LocalDate minJoiningDate = today.plusDays(5);
		LocalDate maxJoiningDate = today.plusDays(60);

		if (joiningDate.isBefore(today)) {
	        throw new IllegalArgumentException("Joining date cannot be in the past.");
	    }
	    if (joiningDate.isBefore(minJoiningDate)) {
	        throw new IllegalArgumentException("Joining date must be at least 5 days after today.");
	    }
	    if (joiningDate.isAfter(maxJoiningDate)) {
	        throw new IllegalArgumentException("Joining date cannot be more than 60 days from today.");
	    }

		Offer offer = new Offer();
		offer.setCandidate(candidate);
		offer.setPosition(position);
		offer.setCtc(ctc);
		offer.setJobLocation(jobLocation);
		offer.setDeadline(LocalDate.parse(deadline));
		offer.setOfferPdf(offerPdf.getBytes());
		offer.setIssuedOn(LocalDate.now());
		offer.setJoiningDate(joiningDate);
		offer.setStatus("Pending");
		offer.setVerificationStatus("Incomplete");
		offer.setCompany(issuerCompany);

		return offerRepository.save(offer);
	}

	@Transactional
	@Override
	public List<Map<String, Object>> getAllOffers() {

		List<Offer> offers = offerRepository.findAll();

		List<Map<String, Object>> response = offers.stream().map(offer -> {
			Map<String, Object> offerDetails = new HashMap<>();
			offerDetails.put("candidateId", offer.getCandidate().getCandidateId());
			offerDetails.put("candidateName", offer.getCandidate().getUsername());
			offerDetails.put("position", offer.getPosition());
			offerDetails.put("offerId", offer.getId());
			offerDetails.put("issuedOn", offer.getIssuedOn());
			offerDetails.put("deadline", offer.getDeadline());
			offerDetails.put("joiningDate", offer.getJoiningDate());
			offerDetails.put("jobLocation", offer.getJobLocation());
			offerDetails.put("ctc", offer.getCtc());
			offerDetails.put("offerStatus", offer.getStatus());
			offerDetails.put("verification", offer.getVerificationStatus());
			offerDetails.put("offerPdf",
					offer.getOfferPdf() != null ? Base64.getEncoder().encodeToString(offer.getOfferPdf()) : null);
			return offerDetails;
		}).toList();

		return response;
	}

	@Transactional
	@Override
	public Optional<Offer> getOfferById(Long id) {
		return offerRepository.findById(id);
	}

	@Transactional
	@Override
	public void deleteByID(Long id) {
		if (offerRepository.existsById(id)) {
			throw new ResourceNotFoundException("ID NOT FOUND :" + id);
		}
		offerRepository.deleteById(id);
	}

	@Transactional
	@Override
	public Offer updateOffer(Long id, String candidateId, String candidateName, MultipartFile offerPdf, String position,
			String ctc, String jobLocation, LocalDate deadline, LocalDate joiningDate) throws IOException {

		Offer existingOffer = offerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Offer not found for ID: " + id));

		if (position != null) {
			existingOffer.setPosition(position);
		}
		if (ctc != null) {
			existingOffer.setCtc(ctc);
		}
		if (jobLocation != null) {
			existingOffer.setJobLocation(jobLocation);
		}
		if (deadline != null) {
			existingOffer.setDeadline(deadline);
		}
		if (joiningDate != null) {
			existingOffer.setJoiningDate(joiningDate);
		}
		if (offerPdf != null && !offerPdf.isEmpty()) {
			existingOffer.setOfferPdf(offerPdf.getBytes());
		}

		Offer savedOffer = offerRepository.save(existingOffer);
		return savedOffer;
	}

}
