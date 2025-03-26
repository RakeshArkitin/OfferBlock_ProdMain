package com.offerblock.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
public class ProjectAssignedRecruiter {

	@Id
	private String id;

	@NotNull(message = "Recruiter name cannot be null")
	private String name;

	@NotNull(message = "Recruiter desiganation cannot be null")
	private String desiganation;

	@ManyToOne
	@JoinColumn(name = "company_id", nullable = false) 
	private Company company;

	@JsonIgnore
	@ManyToMany(mappedBy = "assignedHRs")
	private List<Department> departments = new ArrayList<>();

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

}