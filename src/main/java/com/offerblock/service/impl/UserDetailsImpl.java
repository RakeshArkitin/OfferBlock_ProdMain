package com.offerblock.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.offerblock.entity.Admin;
import com.offerblock.entity.Candidate;
import com.offerblock.entity.Company;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String email;
	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	public static UserDetailsImpl build(Admin admin) {
		List<GrantedAuthority> authorities = admin.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());
		return new UserDetailsImpl(admin.getId(), admin.getEmail(), admin.getPassword(), authorities);
	}

	public static UserDetailsImpl build(Candidate candidate) {
		List<GrantedAuthority> authorities = candidate.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());
		return new UserDetailsImpl(candidate.getId(), candidate.getEmail(), candidate.getPassword(), authorities);
	}

	public static UserDetailsImpl build(Company company) {
		List<GrantedAuthority> authorities = company.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());
		return new UserDetailsImpl(company.getId(), company.getEmail(), company.getPassword(), authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}