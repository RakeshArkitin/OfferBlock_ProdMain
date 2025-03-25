package com.offerblock.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.offerblock.entity.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

	Optional<Candidate> findByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByPanCard(String panCard);

	Optional<Candidate> findByCandidateId(String candidateId);

}
