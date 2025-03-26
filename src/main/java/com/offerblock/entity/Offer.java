package com.offerblock.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "offers")
public class Offer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "candidate_id", nullable = false)
	private Candidate candidate;

	@Column(nullable = false)
	private String position;

	@Column(nullable = false)
	@NotBlank
	private String ctc;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate deadline;

	@Column(nullable = false)
	@NotBlank
	private String jobLocation;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate joiningDate;

	@Lob
	@Column(nullable = false)
	private byte[] offerPdf;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(nullable = false, updatable = false)
	private LocalDate issuedOn = LocalDate.now();

	@Column(nullable = false)
	private String status = "Pending";

	@Column(nullable = false)
	private String verificationStatus = "Incomplete";

	@ManyToOne
	@JoinColumn(name = "company_id", nullable = true)
	private Company company;

	@ManyToOne
	@JoinColumn(name = "recruiter_id", nullable = true)
	private ProjectAssignedRecruiter recruiter;

	@ManyToOne // Ensure this annotation exists
	@JoinColumn(name = "project_id") // This should match your database column
	private Project project;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
