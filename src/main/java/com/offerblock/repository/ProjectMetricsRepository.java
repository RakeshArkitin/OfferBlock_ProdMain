package com.offerblock.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.offerblock.entity.ProjectMetrics;

public interface ProjectMetricsRepository extends JpaRepository<ProjectMetrics, Long> {
    Optional<ProjectMetrics> findByProject_ProjectId(Long projectId);
}
