package com.offerblock.utils;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.offerblock.service.impl.UserDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthTokenFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserDetailServiceImpl userDetailServiceImpl;

	private static final String HEADER_STRING = "Authorization";
	private static final String PREFIX = "Bearer ";

	private String parseJwt(HttpServletRequest request) {
		
		String headerAuth = request.getHeader(HEADER_STRING);
		logger.debug("Authorization Header: {}", headerAuth);
		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(PREFIX)) {
			return headerAuth.substring(PREFIX.length());
		}
		return null;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			
			String jwt = parseJwt(request);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				
				String email = jwtUtils.getUserNameFromJwtToken(jwt);
				
				logger.info("Token validated. Username: {}", email);

				UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(email);
				
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				logger.error("Invalid or expired JWT token");
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e.getMessage());
		}

		filterChain.doFilter(request, response);
	}
}
