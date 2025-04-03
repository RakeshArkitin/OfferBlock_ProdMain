package com.offerblock.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.offerblock.enums.ProjectStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long projectId;

	@NotNull(message = "ProjectName cannot be null")
	private String projectName;

	@Enumerated(EnumType.STRING)
	private ProjectStatus status = ProjectStatus.PENDING;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "budget_id", referencedColumnName = "id")
	private Budget budget;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Department> departments;

	@ManyToMany
	@JoinTable(name = "project_recruiter", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "recruiter_id"))
	private List<AssignedRecruiter> projectHRs;

	@OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	private ProjectMetrics projectMetrics;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Position> positions = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "company_id")
	private Company company;

	private boolean approved = false;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	private int hiringCount = 0;

	public void incrementHiringCount() {
		this.hiringCount += 1;
	}

	public void setStatus(ProjectStatus status) {
		this.status = status;
	}

}
