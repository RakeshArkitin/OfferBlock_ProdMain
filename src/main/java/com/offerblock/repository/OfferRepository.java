package com.offerblock.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.offerblock.entity.Candidate;
import com.offerblock.entity.Offer;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

	List<Offer> findByStatus(String status);

	List<Offer> findByVerificationStatus(String verificationStatus);

	Optional<Offer> findByCandidateAndPositionAndCtc(Candidate candidate, String position, String ctc);

	@Query("SELECT COUNT(o) FROM Offer o WHERE o.project.projectId = :projectId")
	Long countOffersByProject(@Param("projectId") Long projectId);
}
