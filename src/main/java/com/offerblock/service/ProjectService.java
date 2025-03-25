package com.offerblock.service;

import java.util.List;

import com.offerblock.dto.ProjectResponseDTO;
import com.offerblock.entity.Company;
import com.offerblock.entity.Project;

public interface ProjectService {

	public Project saveProject(Project project);

	public Project Update();

	public List<ProjectResponseDTO> getAllProjectResponse(Company company);

//	Project updateProject(Long projectId, Project updatedProject);

	public void deleteProject(String projectName, Company company);

	public Project updateProject(String projectId, Project updatedProject, String companyEmail);


}
