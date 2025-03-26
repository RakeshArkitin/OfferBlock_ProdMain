package com.offerblock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.offerblock.entity.Project;
import com.offerblock.entity.ProjectApprovalRequest;
import com.offerblock.entity.ProjectApprover;

public interface ProjectApprovalRequestRepository extends JpaRepository<ProjectApprovalRequest, Long> {
	List<ProjectApprovalRequest> findByApprover(ProjectApprover approver);

	Optional<Project> findByProject(Project project);
}
