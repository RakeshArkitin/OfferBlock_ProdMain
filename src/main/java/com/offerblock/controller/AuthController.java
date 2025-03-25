package com.offerblock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.offerblock.dto.LoginRequest;
import com.offerblock.service.impl.AuthService;  
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	@Autowired
	public AuthController(AuthService authService) {
		super();
		this.authService = authService;
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/login")
	public ResponseEntity<LoginRequest> login(@Valid @RequestBody LoginRequest loginRequest) {
		return (ResponseEntity<LoginRequest>) authService.authenticate(loginRequest);
	}
}
