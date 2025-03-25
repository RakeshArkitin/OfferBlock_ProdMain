package com.offerblock.utils;

import java.security.Key;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.offerblock.service.impl.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${jwtauthentication.app.jwtSecret}")
	private String jwtSecret;

	@Value("${jwtauthentication.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String generateToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(key(), SignatureAlgorithm.HS256).compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
	}

	private Key key() {
		if (jwtSecret == null || jwtSecret.isEmpty()) {
			throw new IllegalStateException("JWT secret is not configured.");
		}
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	public boolean validateJwtToken(String authToken) {
		try {
			logger.debug("Validating JWT Token: {}", authToken);

			Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);

			logger.info("JWT token validated successfully: {}", claimsJws.getBody());
			return true;
			
		} catch (SecurityException | MalformedJwtException e) {
			logger.error("Invalid JWT Token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT Token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT Token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}

}
