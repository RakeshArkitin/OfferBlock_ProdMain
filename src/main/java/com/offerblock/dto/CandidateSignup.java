package com.offerblock.dto;

import java.util.Set;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CandidateSignup {

	@NotBlank
	@Size(max = 50)
	private String username;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Size(min = 6, max = 40)
	private String password;

	@NotBlank
	@Size(min = 10, max = 10)
	private String panCard;

	@NotNull(message = "Roles cannot be null")
	private Set<String> role;

	public CandidateSignup(@NotBlank @Size(max = 50) String username, @NotBlank @Email String email,
			@NotBlank @Size(min = 6, max = 40) String password, @NotBlank @Size(min = 10, max = 10) String panCard,
			@NotNull(message = "Roles cannot be null") Set<String> role) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.panCard = panCard;
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPanCard() {
		return panCard;
	}

	public void setPanCard(String panCard) {
		this.panCard = panCard;
	}

	public Set<String> getRole() {
		return role;
	}

	public void setRole(Set<String> role) {
		this.role = role;
	}

}
