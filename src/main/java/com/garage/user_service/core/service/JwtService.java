package com.garage.user_service.core.service;

import com.garage.user_service.core.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {

	boolean isTokenExpired(String accessToken);

	String extractUserEmail(String accessToken);

	boolean isTokenValid(String accessToken, UserDetails userDetails);

	String generateAccessToken(User user);

	Map<String, String> getAllClaims(String accessToken);

	boolean validateToken(String token);

}
