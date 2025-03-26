package com.offerblock.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.offerblock.entity.ProjectAssignedRecruiter;

public interface AssignedRecruiterRepository extends JpaRepository<ProjectAssignedRecruiter, String> {

	@EntityGraph(attributePaths = { "departments" })
	Optional<ProjectAssignedRecruiter> findById(String id);
}