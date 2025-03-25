package com.offerblock.response;

public class JwtResponse {

	private String token;
	private String type = "Bearer";

	public JwtResponse(String token, String type) {
		super();
		this.token = token;
		this.type = type;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
