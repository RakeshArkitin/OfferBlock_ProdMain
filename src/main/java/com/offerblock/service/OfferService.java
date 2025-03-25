package com.offerblock.service;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import com.offerblock.entity.Offer;

public interface OfferService {

	public Offer createOffer(String candidateId, String candidateName, String position, String ctc, String jobLocation,
			String deadline, LocalDate joiningDate, MultipartFile offerPdf, Principal principal) throws IOException;

	public List<Map<String, Object>> getAllOffers();

	public Optional<Offer> getOfferById(Long id);

	public void deleteByID(Long id);

	public Offer updateOffer(Long id, String candidateId, String candidateName, MultipartFile offerPdf, String position,
			String ctc, String jobLocation, LocalDate deadline, LocalDate joiningDate) throws IOException;

}
