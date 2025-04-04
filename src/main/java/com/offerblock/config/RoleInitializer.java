package com.offerblock.config;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.offerblock.entity.Role;
import com.offerblock.enums.ERole;
import com.offerblock.repository.RoleRepository;

@Component
public class RoleInitializer implements CommandLineRunner {

	// CommandLineRunner -- runs after the spring boot application starts
	// String..args -- Accepts multiple command-line arguments as an array
	// Args -- Allow passing runtime parameters
	// Component -- component is used automatically detects the class and create
	// bean for dependency injection

	private final RoleRepository roleRepository;

	@Autowired
	public RoleInitializer(RoleRepository roleRepository) {
		super();
		this.roleRepository = roleRepository;
	}

	@Override
	public void run(String... args) {

		List<ERole> roles = Arrays.asList(ERole.ROLE_ADMIN, ERole.ROLE_CANDIDATE, ERole.ROLE_COMPANY,
				ERole.ROLE_RECRUITER, ERole.ROLE_SANCTIONER, ERole.ROLE_APPROVER);

		for (ERole roleEnum : roles) {

			Optional<Role> existingRole = roleRepository.findByName(roleEnum);

			if (existingRole.isEmpty()) {
				Role role = new Role();
				role.setName(roleEnum);
				roleRepository.save(role);
			}
		}
	}
}
