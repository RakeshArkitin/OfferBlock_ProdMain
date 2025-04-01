package com.offerblock.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
	private String token;
	private List<String> role;
	private String userId;
	private String companyName;

	public AuthResponse(String token, List<String> role, String userId) {
		this.token = token;
		this.role = role;
		this.userId = userId;
	}

	public AuthResponse(String token, List<String> role, String userId, String companyName) {
		super();
		this.token = token;
		this.role = role;
		this.userId = userId;
		this.companyName = companyName;
	}

	public String getToken() {
		return token;
	}

	public List<String> getRoles() {
		return role;
	}

	public void setRoles(List<String> role) {
		this.role = role;
	}

	public String getUserId() {
		return userId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
