package com.leedTech.studentFeeOneTimePayment.config.jwtConfig;

import com.leedTech.studentFeeOneTimePayment.exception.BadRequestException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JWTConfig {

@Value("${jwt.secret-key}")
private String jwtSecretKey;

@Value("${jwt.access-token-expiration-ms}")
private long accessTokenExpirationMs;

@Value("${jwt.refresh-token-expiration-ms}")
private long refreshTokenExpirationMs;

@Value("${jwt.magic-link-expiration-ms}")
private long magicLinkExpirationMs;

private static final String ROLE_CLAIM      = "role";
private static final String TOKEN_TYPE_CLAIM = "tokenType";
private static final String ACCESS_TOKEN     = "ACCESS";
private static final String REFRESH_TOKEN    = "REFRESH";

private SecretKey getSigningKey() {
	byte[] keyBytes = jwtSecretKey.getBytes(StandardCharsets.UTF_8);
	if (keyBytes.length < 32) {
		throw new BadRequestException ("JWT secret key must be at least 32 characters long");
	}
	return Keys.hmacShaKeyFor(keyBytes);
}

public String generateAccessToken(String email, String role) {
	Instant now = Instant.now();
	return Jwts.builder()
			       .id(UUID.randomUUID().toString())
			       .subject(email)
			       .claim(ROLE_CLAIM, role)
			       .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN)
			       .issuedAt(Date.from(now))
			       .expiration(Date.from(now.plusMillis(accessTokenExpirationMs)))
			       .signWith(getSigningKey())
			       .compact();
}

public String generateRefreshToken(String email) {
	Instant now = Instant.now();
	return Jwts.builder()
			       .id(UUID.randomUUID().toString())
			       .subject(email)
			       .claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN)
			       .issuedAt(Date.from(now))
			       .expiration(Date.from(now.plusMillis(refreshTokenExpirationMs)))
			       .signWith(getSigningKey())
			       .compact();
}

public Claims validateToken(String token) {
	return Jwts.parser()
			       .verifyWith(getSigningKey())
			       .build()
			       .parseSignedClaims(token)
			       .getPayload();
}

public boolean isTokenValid(String token) {
	try {
		validateToken(token);
		return true;
	} catch (ExpiredJwtException e) {
		throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "Token has expired");
	} catch (JwtException e) {
		throw new JwtException("Invalid token: " + e.getMessage());
	}
}

public boolean isAccessToken(String token) {
	return ACCESS_TOKEN.equals(extractClaim(token, ROLE_CLAIM));
}

public boolean isRefreshToken(String token) {
	return REFRESH_TOKEN.equals(extractClaim(token, TOKEN_TYPE_CLAIM));
}

public String extractEmail(String token) {
	return validateToken(token).getSubject();
}

public String extractRole(String token) {
	return validateToken(token).get(ROLE_CLAIM, String.class);
}

public boolean isTokenExpired(String token) {
	return validateToken(token).getExpiration().before(new Date());
}

public <T> T extractClaim(String token, String claimKey) {
	return (T) validateToken(token).get(claimKey);
}

public long getAccessTokenExpirationSeconds() {
	return accessTokenExpirationMs / 1000;
}

public long getRefreshTokenExpirationSeconds() {
	return refreshTokenExpirationMs / 1000;
}

public String generateMagicLinkToken(String email) {
	return Jwts.builder()
			       .subject(email)
			       .claim("type", "magic-link")
			       .issuedAt(new Date())
			       .expiration(new Date(System.currentTimeMillis() + magicLinkExpirationMs))
			       .signWith(getSigningKey())
			       .compact();
}

public String extractEmailFromMagicLinkToken(String token) {
	return Jwts.parser()
			       .verifyWith(getSigningKey())
			       .build()
			       .parseSignedClaims(token)
			       .getPayload()
			       .getSubject();
}

public boolean isMagicLinkToken(String token) {
	try {
		Claims claims = Jwts.parser()
				                .verifyWith(getSigningKey())
				                .build()
				                .parseSignedClaims(token)
				                .getPayload();
		return "magic-link".equals(claims.get("type", String.class));
	} catch (JwtException e) {
		return false;
	}
}

public boolean isMagicLinkTokenValid(String token) {
	try {
		Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token);
		return isMagicLinkToken(token);
	} catch (JwtException | IllegalArgumentException e) {
		return false;
	}
}
}
