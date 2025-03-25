package com.offerblock.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.offerblock.enums.ERole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "candidates", uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
public class Candidate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false, updatable = false)
	private String candidateId;

	@NotBlank
	@Size(max = 50)
	private String username;

	@NotBlank
	@Email
	@Column(unique = true)
	private String email;

	@NotBlank
	@Size(max = 120)
	private String password;

	@NotBlank
	@Size(min = 10, max = 10)
	@Column(unique = true)
	private String panCard;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "candidate_roles", joinColumns = @JoinColumn(name = "candidate_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@Column(nullable = false)
	private boolean hired = false;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public Candidate(String candidateId,
			@Size(max = 50) @NotBlank @Pattern(regexp = "^[A-Za-z][A-Za-z0-9 ]*$", message = "Username cannot start with a number") String username,
			@NotBlank @Email String email, @Size(max = 120) @NotBlank String password,
			@Size(min = 10, max = 10) @NotBlank String panCard) {
		this.candidateId = candidateId;
		this.username = username;
		this.email = email;
		this.password = password;
		this.panCard = panCard;
	}

	public boolean hasRecruiterRole() {
		return roles.stream().anyMatch(role -> role.getName().equals(ERole.ROLE_RECRUITER));
	}

	public void assignRecruiterRole() {
		Role recruiterRole = new Role();
		recruiterRole.setName(ERole.ROLE_RECRUITER);
		this.roles.add(recruiterRole);
	}

	public boolean hasApproverRole() {
		return roles.stream().anyMatch(role -> role.getName().equals(ERole.ROLE_APPROVER));
	}

	public void assignApproverRole() {
		Role approverRole = new Role();
		approverRole.setName(ERole.ROLE_APPROVER);
		this.roles.add(approverRole);
	}

	public boolean hasSanctionerRole() {
		return roles.stream().anyMatch(role -> role.getName().equals(ERole.ROLE_SANCTIONER));
	}

	public void assignSanctionerRole() {
		Role sanctionerRole = new Role();
		sanctionerRole.setName(ERole.ROLE_SANCTIONER);
		this.roles.add(sanctionerRole);
	}

	@OneToMany(mappedBy = "candidate")
	private List<Recruiter> recruiterHistory;

	@OneToMany(mappedBy = "candidate")
	private List<ProjectApprover> approverHistory;

}
