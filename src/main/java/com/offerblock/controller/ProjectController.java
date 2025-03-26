package com.offerblock.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.offerblock.dto.PositionDTO;
import com.offerblock.dto.ProjectResponseDTO;
import com.offerblock.dto.ProjectWithPositionsDTO;
import com.offerblock.entity.Budget;
import com.offerblock.entity.Candidate;
import com.offerblock.entity.Company;
import com.offerblock.entity.Project;
import com.offerblock.entity.Recruiter;
import com.offerblock.enums.BudgetStatus;
import com.offerblock.enums.ProjectStatus;
import com.offerblock.exception.ResourceNotFoundException;
import com.offerblock.repository.CandidateRepository;
import com.offerblock.repository.CompanyRepository;
import com.offerblock.repository.OfferRepository;
import com.offerblock.repository.ProjectRepository;
import com.offerblock.repository.RecruiterRepository;
import com.offerblock.service.ProjectService;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin("*")
public class ProjectController {

	private final ProjectService projectService;
	private final CompanyRepository companyRepository;
	private final ProjectRepository projectRepository;
	private final OfferRepository offerRepository;
	
	@Autowired
	private CandidateRepository candidateRepository;
	
	@Autowired
	private RecruiterRepository recruiterRepository;

	@Autowired
	public ProjectController(ProjectService projectService, CompanyRepository companyRepository,
			ProjectRepository projectRepository, OfferRepository offerRepository) {
		super();
		this.projectService = projectService;
		this.companyRepository = companyRepository;
		this.projectRepository = projectRepository;
		this.offerRepository = offerRepository;
	}

	@PreAuthorize("hasAnyRole('COMPANY','RECRUITER')")
	@PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createProject(@RequestBody Project project, Principal principal) {

	    String email = principal.getName(); // Logged-in user's email

	    // Step 1: Try to fetch the company user
	    Optional<Company> companyOpt = companyRepository.findByEmail(email);

	    if (companyOpt.isPresent()) {
	        // Company is creating the project
	        project.setCompany(companyOpt.get());
	    } else {
	        // Step 2: If not a company, check if it's a recruiter
	        Optional<Candidate> candidateOpt = candidateRepository.findByEmail(email);
	        if (candidateOpt.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Candidate not found");
	        }

	        Optional<Recruiter> recruiterOpt = recruiterRepository.findByCandidate(candidateOpt.get());
	        if (recruiterOpt.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Recruiter not found");
	        }

	        Recruiter recruiter = recruiterOpt.get();
	        if (!recruiter.isActive()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Recruiter is not active");
	        }

	        project.setCompany(recruiter.getCompany());
	        project.setCreatedByRecruiter(recruiter); // Optional: track who created it
	    }

	    project.setStatus(ProjectStatus.PENDING);

	    projectService.saveProject(project);
	    return ResponseEntity.ok("Project created successfully!!");
	}

	@PreAuthorize("hasRole('COMPANY')")
	@GetMapping("/getAll")
	public List<ProjectResponseDTO> getAllProjects(Principal principal) {
		String email = principal.getName();
		Optional<Company> company = companyRepository.findByEmail(email);

		if (company.isPresent()) {
			return projectService.getAllProjectResponse(company.get());
		}

		return Collections.emptyList();
	}

	@PreAuthorize("hasRole('COMPANY')")
	@GetMapping("/getAllprojects")
	public ResponseEntity<List<ProjectWithPositionsDTO>> getCompanyProjects(Principal principal) {

		String companyEmail = principal.getName();
		Company company = companyRepository.findByEmail(companyEmail)
				.orElseThrow(() -> new IllegalArgumentException("Company not found"));

		List<ProjectWithPositionsDTO> projects = projectRepository.findByCompany(company).stream().map(project -> {

			List<PositionDTO> positions = project.getPositions().stream()
					.map(position -> new PositionDTO(position.getPositionName(), position.getVacancies()))
					.collect(Collectors.toList());

			return new ProjectWithPositionsDTO(project.getProjectId(), project.getProjectName(), positions);
		}).collect(Collectors.toList());

		return ResponseEntity.ok(projects);
	}

	@PreAuthorize("hasRole('COMPANY')")
	@PutMapping("/update/{projectName}")
	public ResponseEntity<?> updateProject(@PathVariable String projectName, @RequestBody Project updatedProject,
			Principal principal) {

		String companyEmail = principal.getName();

		try {
			Project project = projectService.updateProject(projectName, updatedProject, companyEmail);
			return ResponseEntity.ok(project);
		} catch (ResourceNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		} catch (AccessDeniedException ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating project: " + ex.getMessage());
		}
	}

	@PreAuthorize("hasRole('COMPANY')")
	@DeleteMapping("/delete/{projectName}")
	public ResponseEntity<String> deleteProjectByName(@PathVariable String projectName, Principal principal) {
		String companyEmail = principal.getName();

		Company company = companyRepository.findByEmail(companyEmail)
				.orElseThrow(() -> new RuntimeException("Company not found"));

		Project project = projectRepository.findByProjectName(projectName)
				.orElseThrow(() -> new RuntimeException("Project not found"));

		// Ensure project belongs to the authenticated company
		if (!project.getCompany().getCompanyId().equals(company.getCompanyId())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this project.");
		}

		projectRepository.delete(project);

		return ResponseEntity.ok("Project '" + projectName + "' deleted successfully.");
	}

	@PreAuthorize("hasRole('APPROVER')")
	@PutMapping("/approve/{projectId}")
	public ResponseEntity<?> approveProject(@PathVariable Long projectId) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));

		project.setStatus(ProjectStatus.APPROVED);
		projectRepository.save(project);

		return ResponseEntity.ok("Project approved successfully!");
	}

	@PreAuthorize("hasRole('SANCTIONER')")
	@PutMapping("/sanctionBudget/{projectId}")
	public ResponseEntity<?> sanctionBudget(@PathVariable Long projectId, @RequestBody Budget budgetUpdate) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));

		if (!ProjectStatus.APPROVED.equals(project.getStatus())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Project must be approved first");
		}

		Budget budget = project.getBudget();
		budget.setTotalDisbursement(budgetUpdate.getTotalDisbursement());
		budget.setOnboardingExpenses(budgetUpdate.getOnboardingExpenses());
		budget.setMiscellaneousExpenses(budgetUpdate.getMiscellaneousExpenses());

		budget.setStatus(BudgetStatus.SANCTIONED);

		return ResponseEntity.ok("Budget sanctioned successfully!");
	}

	@PreAuthorize("hasRole('COMPANY')")
	@DeleteMapping("/delete/{projectId}")
	public ResponseEntity<String> deleteProject(@PathVariable Long projectId, Principal principal) {

		String companyEmail = principal.getName();
		Company company = companyRepository.findByEmail(companyEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Company not found"));

		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));

		if (!project.getCompany().getCompanyId().equals(company.getCompanyId())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot delete this project");
		}

		Long offerCount = offerRepository.countOffersByProject(projectId);
		if (offerCount > 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot delete project with active offers");
		}

		projectRepository.delete(project);
		return ResponseEntity.ok("Project deleted successfully");
	}

	@PreAuthorize("hasAnyRole('RECRUITER','COMPANY')")
	@PostMapping("/{projectId}/send-approval-request")
	public ResponseEntity<String> sendApprovalRequest(@PathVariable Long projectId,
			@RequestParam String requestedById) {
		projectService.sendProjectApprovalRequest(projectId, requestedById);
		return ResponseEntity.ok("Approval request send successfully");
	}

}
