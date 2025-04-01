package com.offerblock.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.offerblock.entity.AssignedRecruiter;

public interface AssignedRecruiterRepository extends JpaRepository<AssignedRecruiter, String> {

	@EntityGraph(attributePaths = { "departments" })
	Optional<AssignedRecruiter> findById(String id);
}