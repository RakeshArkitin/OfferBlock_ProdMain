package com.offerblock.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.offerblock.dto.DepartmentDTO;
import com.offerblock.dto.PositionDTO;
import com.offerblock.dto.ProjectResponseDTO;
import com.offerblock.dto.RecruiterDTO;
import com.offerblock.entity.AssignedRecruiter;
import com.offerblock.entity.Budget;
import com.offerblock.entity.BudgetSanctionRequest;
import com.offerblock.entity.BudgetSanctioner;
import com.offerblock.entity.Company;
import com.offerblock.entity.Department;
import com.offerblock.entity.Position;
import com.offerblock.entity.Project;
import com.offerblock.entity.ProjectApprovalRequest;
import com.offerblock.entity.ProjectApprover;
import com.offerblock.entity.ProjectMetrics;
import com.offerblock.enums.ApprovalStatus;
import com.offerblock.enums.BudgetStatus;
import com.offerblock.enums.ProjectStatus;
import com.offerblock.exception.DuplicateValueExistsException;
import com.offerblock.exception.ResourceNotFoundException;
import com.offerblock.repository.AssignedRecruiterRepository;
import com.offerblock.repository.BudgetSanctionRequestRepository;
import com.offerblock.repository.BudgetSanctionerRepository;
import com.offerblock.repository.CompanyRepository;
import com.offerblock.repository.DepartmentRepository;
import com.offerblock.repository.OfferRepository;
import com.offerblock.repository.PositionRepository;
import com.offerblock.repository.ProjectApprovalRequestRepository;
import com.offerblock.repository.ProjectApproverRepository;
import com.offerblock.repository.ProjectMetricsRepository;
import com.offerblock.repository.ProjectRepository;
import com.offerblock.service.ProjectService;

import jakarta.transaction.Transactional;

@Service
public class ProjectServiceImpl implements ProjectService {

	private final ProjectRepository projectRepository;
	private final AssignedRecruiterRepository recruiterRepository;
	private final DepartmentRepository departmentRepository;
	private final ProjectMetricsRepository projectMetricsRepository;
	private final OfferRepository offerRepository;
	private final PositionRepository positionRepository;
	private final CompanyRepository companyRepository;
	private final BudgetSanctionerRepository budgetSanctionerRepository;
	private final BudgetSanctionRequestRepository budgetSanctionRequestRepository;
	private final ProjectApprovalRequestRepository projectApprovalRequestRepository;
	private final ProjectApproverRepository projectApproverRepository;

	@Autowired
	public ProjectServiceImpl(ProjectRepository projectRepository, AssignedRecruiterRepository recruiterRepository,
			DepartmentRepository departmentRepository, ProjectMetricsRepository projectMetricsRepository,
			OfferRepository offerRepository, PositionRepository positionRepository, CompanyRepository companyRepository,
			BudgetSanctionerRepository budgetSanctionerRepository,
			BudgetSanctionRequestRepository budgetSanctionRequestRepository,
			ProjectApproverRepository projectApproverRepository,
			ProjectApprovalRequestRepository projectApprovalRequestRepository) {
		super();
		this.projectRepository = projectRepository;
		this.recruiterRepository = recruiterRepository;
		this.departmentRepository = departmentRepository;
		this.projectMetricsRepository = projectMetricsRepository;
		this.offerRepository = offerRepository;
		this.positionRepository = positionRepository;
		this.companyRepository = companyRepository;
		this.budgetSanctionerRepository = budgetSanctionerRepository;
		this.budgetSanctionRequestRepository = budgetSanctionRequestRepository;
		this.projectApprovalRequestRepository = projectApprovalRequestRepository;
		this.projectApproverRepository = projectApproverRepository;
	}

	@Override
	@Transactional
	public Project saveProject(Project project) {

		// Step 1: Check if the project name already exists
		if (projectRepository.findByProjectName(project.getProjectName()).isPresent()) {
			throw new DuplicateValueExistsException("Project already exists");
		}

		// Step 2: Ensure the budget object is not null
		if (project.getBudget() == null) {
			project.setBudget(new Budget());
		}

		// Step 3: Save the project entity in the database
		Project savedProject = projectRepository.save(project);

		// Step 4: Process departments and assigned recruiters
		if (project.getDepartments() != null) {
			List<Department> savedDepartments = new ArrayList<>();
			for (Department dept : project.getDepartments()) {
				dept.setProject(savedProject); // Associate department with the project

				// Step 4.1: Process assigned recruiters for the department
				if (dept.getAssignedHRs() != null && !dept.getAssignedHRs().isEmpty()) {
					List<AssignedRecruiter> validRecruiters = new ArrayList<>();
					for (AssignedRecruiter recruiter : dept.getAssignedHRs()) {
						AssignedRecruiter existingRecruiter = recruiterRepository.findById(recruiter.getId())
								.orElse(null);

						if (existingRecruiter == null) {
							// 🔹 Recruiter doesn't exist, create a new one
							existingRecruiter = new AssignedRecruiter();
							existingRecruiter.setId(recruiter.getId()); // Assigning ID from candidate
							existingRecruiter.setName(recruiter.getName());
							existingRecruiter.setDesiganation(recruiter.getDesiganation());
							existingRecruiter.setCompany(savedProject.getCompany()); // Assign company
							existingRecruiter = recruiterRepository.save(existingRecruiter); // Save recruiter
						}

						validRecruiters.add(existingRecruiter);
					}
					dept.setAssignedHRs(validRecruiters);
				}

				savedDepartments.add(departmentRepository.save(dept));
			}
			savedProject.setDepartments(savedDepartments);
		}

		// Step 5: Process department positions
		if (project.getDepartments() != null) {
			for (Department department : project.getDepartments()) {
				if (department.getPositions() != null) {
					for (Position position : department.getPositions()) {
						position.setDepartment(department);
						position.setProject(savedProject);
						positionRepository.save(position);
					}
				}
			}
		}

		// Step 6: Process project recruiters
		List<AssignedRecruiter> validProjectRecruiters = new ArrayList<>();
		if (project.getProjectHRs() != null) {
			for (AssignedRecruiter recruiter : project.getProjectHRs()) {
				AssignedRecruiter existingRecruiter = recruiterRepository.findById(recruiter.getId()).orElse(null);

				if (existingRecruiter == null) {
					// 🔹 Create new recruiter if they don't exist
					existingRecruiter = new AssignedRecruiter();
					existingRecruiter.setId(recruiter.getId());
					existingRecruiter.setName(recruiter.getName());
					existingRecruiter.setDesiganation(recruiter.getDesiganation());
					existingRecruiter.setCompany(savedProject.getCompany());
					existingRecruiter = recruiterRepository.save(existingRecruiter);
				}

				validProjectRecruiters.add(existingRecruiter);
			}
		}
		savedProject.setProjectHRs(validProjectRecruiters);

		savedProject = projectRepository.save(savedProject); // Final save with recruiters

		// ✅ Step 7: Send approval request after project creation
		List<ProjectApprover> approvers = projectApproverRepository.findByCompany(savedProject.getCompany());

		ProjectApprovalRequest approvalRequest = new ProjectApprovalRequest();
		approvalRequest.setProject(savedProject);
		approvalRequest.setCompany(savedProject.getCompany());

		approvalRequest.setRequestedBy(savedProject.getProjectHRs().isEmpty() ? savedProject.getCompany().getCompanyId()
				: savedProject.getProjectHRs().get(0).getId());

		// ✅ Send to the first available approver
		if (approvers != null && !approvers.isEmpty()) {
			approvalRequest.setApprover(approvers.get(0)); // send to first approver only
			approvalRequest.setStatus(ApprovalStatus.PENDING);
		} else {
			// ❗ No approver available → pending assignment
			approvalRequest.setApprover(null);
			approvalRequest.setStatus(ApprovalStatus.PENDING_APPROVER_ASSIGNMENT);
		}

		if (approvalRequest.getStatus() == ApprovalStatus.APPROVED) {
			List<BudgetSanctioner> sanctioners = budgetSanctionerRepository.findByCompany(savedProject.getCompany());

			if (!sanctioners.isEmpty()) {
				BudgetSanctionRequest budgetRequest = new BudgetSanctionRequest();
				budgetRequest.setProject(savedProject);
				budgetRequest.setCompany(savedProject.getCompany());
				budgetRequest.setSanctioner(sanctioners.get(0));
				budgetRequest.setStatus(BudgetStatus.PENDING);
				budgetSanctionRequestRepository.save(budgetRequest);
			}
		}

		projectApprovalRequestRepository.save(approvalRequest);

		return savedProject;
	}

	@Transactional
	@Override
	public Project updateProject(String projectName, Project updatedProject, String companyEmail) {

		Company company = companyRepository.findByEmail(companyEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		Project existingProject = projectRepository.findByProjectName(projectName)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found with name: " + projectName));

		if (!existingProject.getCompany().getId().equals(company.getId())) {
			throw new AccessDeniedException("You do not have permission to update this project");
		}

		// Update Basic Fields
		existingProject.setProjectName(updatedProject.getProjectName());

		// Update Budget
		if (updatedProject.getBudget() != null) {
			if (existingProject.getBudget() == null) {
				existingProject.setBudget(new Budget());
			}
			existingProject.getBudget().setTotalDisbursement(updatedProject.getBudget().getTotalDisbursement());
			existingProject.getBudget().setOnboardingExpenses(updatedProject.getBudget().getOnboardingExpenses());
			existingProject.getBudget().setMiscellaneousExpenses(updatedProject.getBudget().getMiscellaneousExpenses());
		}

		// ✅ Update Existing Departments Instead of Creating New Ones
		if (updatedProject.getDepartments() != null) {
			List<Department> updatedDepartments = new ArrayList<>();
			for (Department updatedDept : updatedProject.getDepartments()) {
				Department existingDept = existingProject.getDepartments().stream()
						.filter(d -> d.getDepartmentName().equals(updatedDept.getDepartmentName())).findFirst()
						.orElse(new Department()); // Create new department if not found

				existingDept.setDepartmentName(updatedDept.getDepartmentName());
				existingDept.setProject(existingProject); // ✅ Ensure the department is linked to the project

				// ✅ Update Assigned Recruiters
				existingDept.getAssignedHRs().clear();
				if (updatedDept.getAssignedHRs() != null) {
					existingDept.getAssignedHRs().addAll(updatedDept.getAssignedHRs());
				}

				// ✅ Update Positions
				existingDept.getPositions().clear();
				if (updatedDept.getPositions() != null) {
					for (Position updatedPosition : updatedDept.getPositions()) {
						Position existingPosition = new Position();
						existingPosition.setPositionName(updatedPosition.getPositionName());
						existingPosition.setVacancies(updatedPosition.getVacancies());
						existingPosition.setDepartment(existingDept); // ✅ Ensure department_id is set
						existingDept.getPositions().add(existingPosition);
					}
				}

				updatedDepartments.add(existingDept);
			}

			existingProject.setDepartments(updatedDepartments);
		}

		// ✅ Update Project Recruiters
		if (updatedProject.getProjectHRs() != null) {
			existingProject.getProjectHRs().clear();
			existingProject.getProjectHRs().addAll(updatedProject.getProjectHRs());
		}

		return projectRepository.save(existingProject);
	}

	@Transactional
	@Override
	public List<ProjectResponseDTO> getAllProjectResponse(Company company) {

		return projectRepository.findByCompany(company).stream().map(project -> {

			Long offerCount = offerRepository.countOffersByProject(project.getProjectId());

			int hiringCount = projectMetricsRepository.findByProject_ProjectId(project.getProjectId())
					.map(ProjectMetrics::getHiringCount).orElse(0);

			List<RecruiterDTO> projectHRs = project.getProjectHRs().stream()
					.map(hr -> new RecruiterDTO(hr.getId(), hr.getName(), hr.getDesiganation()))
					.collect(Collectors.toList());

			List<DepartmentDTO> departments = project.getDepartments().stream().map(department -> {
				List<RecruiterDTO> assignedHRs = department.getAssignedHRs().stream()
						.map(hr -> new RecruiterDTO(hr.getId(), hr.getName(), hr.getDesiganation()))
						.collect(Collectors.toList());

				List<PositionDTO> positions = department.getPositions().stream()
						.map(position -> new PositionDTO(position.getPositionName(), position.getVacancies()))
						.collect(Collectors.toList());

				return new DepartmentDTO(department.getDepartmentName(), assignedHRs, positions);
			}).collect(Collectors.toList());

			int totalVacancies = departments.stream().flatMap(dept -> dept.getPositions().stream())
					.mapToInt(PositionDTO::getVacancies).sum();
			String status = (hiringCount >= totalVacancies) ? "Completed" : "In Progress";

			return new ProjectResponseDTO(project.getProjectId(), project.getProjectName(), project.getBudget(),
					departments, projectHRs, offerCount.intValue(), hiringCount, status);
		}).collect(Collectors.toList());
	}

	@Transactional
	@Override
	public void deleteProject(String projectName, Company company) {

		Project project = projectRepository.findByProjectNameAndCompany(projectName, company)
				.orElseThrow(() -> new IllegalArgumentException("Project not found or not owned by the company."));

		if (!project.getCompany().getId().equals(company.getId())) {
			throw new IllegalArgumentException("You do not have permission to delete this project.");
		}

		departmentRepository.deleteAll(project.getDepartments());
		projectRepository.delete(project);
	}
	
	@Transactional
	@Override
	public void approveProject(Long projectId) {
		
	    Project project = projectRepository.findById(projectId)
	            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	    
	    if (!project.getStatus().equals(ProjectStatus.PENDING)) {
	        throw new IllegalStateException("Project cannot be approved in its current status: " + project.getStatus());
	    }

	    project.setStatus(ProjectStatus.APPROVED);
	    projectRepository.save(project);

	    List<BudgetSanctioner> sanctioners = budgetSanctionerRepository.findByCompany(project.getCompany());

	    BudgetSanctionRequest budgetRequest = new BudgetSanctionRequest();
	    budgetRequest.setProject(project);
	    budgetRequest.setCompany(project.getCompany());

	    if (!sanctioners.isEmpty()) {
	        budgetRequest.setSanctioner(sanctioners.get(0));
	        budgetRequest.setStatus(BudgetStatus.PENDING);
	    } else {
	        budgetRequest.setSanctioner(null);
	        budgetRequest.setStatus(BudgetStatus.PENDING_SANCTIONER_ASSIGNMENT);
	    }

	    budgetSanctionRequestRepository.save(budgetRequest);
	}


}