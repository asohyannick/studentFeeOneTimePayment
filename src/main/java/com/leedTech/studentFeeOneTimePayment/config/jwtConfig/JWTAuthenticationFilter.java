package com.leedTech.studentFeeOneTimePayment.config.jwtConfig;

import com.leedTech.studentFeeOneTimePayment.constant.UserRole;
import com.leedTech.studentFeeOneTimePayment.exception.BadRequestException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

private final JWTConfig jwtConfig;

private static final String BEARER_PREFIX       = "Bearer ";
private static final String ACCESS_TOKEN_COOKIE = "accessToken";


@Override
protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
) throws ServletException, IOException {
	
	try {
		String token = resolveToken(request);
		
		if (StringUtils.hasText(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
			processAuthentication(token, request);
		}
		
	} catch (ExpiredJwtException e) {
		log.warn("JWT token has expired: {}", e.getMessage());
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
		return;
		
	} catch (JwtException e) {
		log.warn("Invalid JWT token: {}", e.getMessage());
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
		return;
		
	} catch (Exception e) {
		log.error("Authentication error: {}", e.getMessage());
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication error");
		return;
	}
	
	filterChain.doFilter(request, response);
}

// ─── Token Resolution (Cookie first, then Header) ───────────────

private String resolveToken(HttpServletRequest request) {
	
	// 1. Try cookie first
	if (request.getCookies() != null) {
		Optional<Cookie> jwtCookie = Arrays.stream(request.getCookies())
				                             .filter(c -> ACCESS_TOKEN_COOKIE.equals(c.getName()))
				                             .findFirst();
		if (jwtCookie.isPresent()) {
			return jwtCookie.get().getValue();
		}
	}
	
	String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
	if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
		return bearerToken.substring(BEARER_PREFIX.length());
	}
	
	return null;
}

// ─── Authentication Processing ──────────────────────────────────

private void processAuthentication(String token, HttpServletRequest request) {
	Claims claims  = jwtConfig.validateToken(token);
	String email   = claims.getSubject();
	String roleStr = claims.get("role", String.class);
	
	if (!StringUtils.hasText(email)) {
		log.warn("JWT token has no subject");
		return;
	}
	
	UserRole userRole = parseRole(roleStr);
	String authority  = "ROLE_" + userRole.name();
	
	UsernamePasswordAuthenticationToken authToken =
			new UsernamePasswordAuthenticationToken(
					email,
					null,
					Collections.singletonList(new SimpleGrantedAuthority(authority))
			);
	
	authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	SecurityContextHolder.getContext().setAuthentication(authToken);
	log.debug("Authenticated user: {} with role: {}", email, userRole);
}

private UserRole parseRole(String roleStr) {
	if (!StringUtils.hasText(roleStr)) {
		log.warn("No role found in JWT, defaulting to STUDENT");
		return UserRole.STUDENT;
	}
	try {
		return UserRole.valueOf(roleStr);
	} catch ( BadRequestException e) {
		log.warn("Unknown role '{}' in JWT, defaulting to STUDENT", roleStr);
		return UserRole.STUDENT;
	}
}
}
