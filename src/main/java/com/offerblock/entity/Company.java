package com.offerblock.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "companies", uniqueConstraints = { @UniqueConstraint(columnNames = "username") })
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false, updatable = false)
	private String companyId;

	@NotBlank
	@Size(max = 120)
	private String password;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Size(max = 100)
	private String companyName;

	@NotBlank
	@Size(max = 255)
	private String companyAddress;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "company_roles", joinColumns = { @JoinColumn(name = "company_id") }, inverseJoinColumns = {
			@JoinColumn(name = "role_id") })
	private Set<Role> roles = new HashSet<>();

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	private boolean isActive = true;

	public Company(String companyId, String password, String email, String companyName, String companyAddress) {
		this.companyId = companyId;
		this.password = password;
		this.email = email;
		this.companyName = companyName;
		this.companyAddress = companyAddress;
	}
}
