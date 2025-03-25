package com.offerblock.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.offerblock.entity.Company;
import com.offerblock.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

	Optional<Project> findByProjectName(String projectName);

	List<Project> findByCompany(Company company);

	Optional<Project> findByProjectNameAndCompany(String projectName, Company company);

}
