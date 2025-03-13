package com.garage.user_service.core.component;

import com.garage.user_service.core.service.JwtService;
import com.garage.user_service.core.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@NonNull
	private final JwtService jwtService;

	@NonNull
	private final UserService userService;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		log.debug("JwtAuthenticationFilter.doFilterInternal(): executing started");

		final String authHeader = request.getHeader("Authorization");
		final String accessToken;
		final String email;
		if (authHeader == null || Objects.equals(authHeader, "") || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		accessToken = authHeader.substring(7);
		if (jwtService.isTokenExpired(accessToken)) {
			log.error("JwtAuthenticationFilter.doFilterInternal(): JWT token expired");
			return;
		}

		email = jwtService.extractUserEmail(accessToken);
		if (!email.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userService.getUserDetailsService().loadUserByUsername(email);
			if (jwtService.isTokenValid(accessToken, userDetails)) {

				List<SimpleGrantedAuthority> authorities = userDetails.getAuthorities()
					.stream()
					.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
					.toList();

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, authorities);
				SecurityContext context = SecurityContextHolder.createEmptyContext();
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				context.setAuthentication(authentication);
				SecurityContextHolder.setContext(context);
			}
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
		log.debug("JwtAuthenticationFilter.shouldNotFilter(): checking request URI {}", request.getRequestURI());

		List<String> unfilteredEndpoints = List.of("/api/v1/auth/sign-in", "/api/v1/auth/sign-up",
				"/api/v1/auth/admin/sign-up", "/api/v1/auth/forgot-password", "/api/v1/auth/verify/otp");

		String requestURI = request.getRequestURI();
		boolean shouldNotFilter = unfilteredEndpoints.contains(requestURI);

		log.debug("JwtAuthenticationFilter.shouldNotFilter(): skipping filter = {}", shouldNotFilter);
		return shouldNotFilter;
	}

}
