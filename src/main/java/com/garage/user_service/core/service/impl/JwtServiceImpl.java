package com.garage.user_service.core.service.impl;

import com.garage.user_service.core.model.User;
import com.garage.user_service.core.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

	@Value("${jwt.signing.key}")
	private String signingKey;

	@Value("${jwt.signing.expiration}")
	private String expirationTime;

	@Override
	public boolean isTokenExpired(String accessToken) {
		return getTokenExpirationTime(accessToken).before(new Date());
	}

	private Date getTokenExpirationTime(String accessToken) {
		return extractClaim(accessToken, Claims::getExpiration);
	}

	@Override
	public String extractUserEmail(String accessToken) {
		try {
			return extractClaim(accessToken, Claims::getSubject);
		}
		catch (Exception e) {
			log.error("JwtServiceImpl.extractUserEmail(): error occurred when extracting user email: {}",
					e.getMessage());
			return null;
		}
	}

	@Override
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String userName = extractUserEmail(token);
		return userName != null && userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	@Override
	public String generateAccessToken(User user) {
		return generateAccessToken(new HashMap<>(), user);
	}

	@Override
	public Map<String, String> getAllClaims(String accessToken) {
		Claims claims = extractAllClaims(accessToken);
		return claims.entrySet()
			.stream()
			.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toString()));
	}

	@Override
	public boolean validateToken(String token) {
		try {
			if (isTokenExpired(token)) {
				log.error("JwtServiceImpl.validateToken(): Token has expired.");
				return false;
			}

			String userEmail = extractUserEmail(token);
			if (userEmail == null || userEmail.isEmpty()) {
				log.error("JwtServiceImpl.validateToken(): Token does not contain a valid subject (user email).");
				return false;
			}

			Claims claims = extractAllClaims(token);
			String role = claims.get("role", String.class);
			if (role == null || role.isEmpty()) {
				log.error("JwtServiceImpl.validateToken(): Token does not contain a valid role.");
				return false;
			}

			return true;
		}
		catch (Exception e) {
			log.error("JwtServiceImpl.validateToken(): Token validation failed: {}", e.getMessage(), e);
			return false;
		}
	}

	private String generateAccessToken(Map<String, Object> extraClaims, User user) {
		String role = user.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse("");

		Map<String, Object> claims = new HashMap<>();
		claims.put("role", role);
		claims.put("userId", user.getUserId());
		claims.put("username", user.getUsername());

		if (extraClaims != null) {
			claims.putAll(extraClaims);
		}

		long expirationInMillis = Long.parseLong(expirationTime);
		Date expirationDate = new Date(System.currentTimeMillis() + expirationInMillis);

		return Jwts.builder()
			.claims(claims)
			.subject(user.getEmail())
			.issuedAt(new Date())
			.expiration(expirationDate)
			.signWith(getSignInKey())
			.compact();
	}

	private SecretKey getSignInKey() {
		byte[] keyBytes = java.util.Base64.getDecoder().decode(signingKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	private <T> T extractClaim(String accessToken, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(accessToken);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String accessToken) {
		return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(accessToken).getPayload();
	}

}
