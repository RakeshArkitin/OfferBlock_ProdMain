package com.offerblock.dto;

import java.util.Set;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CompanySignup {

	@NotBlank
	@Size(min = 6, max = 40)
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

	@NotNull(message = "Roles cannot be null")
	private Set<String> role;

	public CompanySignup(@NotBlank @Size(min = 6, max = 40) String password, @NotBlank @Email String email,
			@NotBlank @Size(max = 100) String companyName, @NotBlank @Size(max = 255) String companyAddress,
			@NotNull(message = "Roles cannot be null") Set<String> role) {
		super();
		this.password = password;
		this.email = email;
		this.companyName = companyName;
		this.companyAddress = companyAddress;
		this.role = role;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public Set<String> getRole() {
		return role;
	}

	public void setRole(Set<String> role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
