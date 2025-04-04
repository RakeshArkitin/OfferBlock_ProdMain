package com.offerblock.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.offerblock.entity.Candidate;
import com.offerblock.entity.Company;
import com.offerblock.entity.Project;
import com.offerblock.entity.ProjectApprovalRequest;
import com.offerblock.entity.ProjectApprover;
import com.offerblock.enums.ApprovalStatus;

public interface ProjectApprovalRequestRepository extends JpaRepository<ProjectApprovalRequest, Long> {

	List<ProjectApprovalRequest> findByApprover(ProjectApprover approver);

	Optional<ProjectApprovalRequest> findByProject(Project project);

	@Query("SELECT p FROM ProjectApprovalRequest p WHERE p.approver.candidate = :candidate AND p.status = :status")
    List<ProjectApprovalRequest> findByCandidateAndStatus(@Param("candidate") Candidate candidate,
                                                          @Param("status") ApprovalStatus status);

    @Query("SELECT p FROM ProjectApprovalRequest p WHERE p.company = :company AND p.status = :status")
    List<ProjectApprovalRequest> findByCompanyAndStatus(@Param("company") Company company,
                                                        @Param("status") ApprovalStatus status);
}
