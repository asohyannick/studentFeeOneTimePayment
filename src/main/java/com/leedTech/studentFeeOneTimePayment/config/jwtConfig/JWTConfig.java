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
private static final long PASSWORD_RESET_EXPIRATION = 1000 * 60 * 5L;

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

public boolean isPasswordResetToken(String token) {
	try {
		Claims claims = Jwts.parser()
				                .verifyWith(getSigningKey())
				                .build()
				                .parseSignedClaims(token)
				                .getPayload();
		
		return "PASSWORD_RESET".equals(claims.get("type", String.class));
	} catch (JwtException e) {
		return false;
	}
}

public String generatePasswordResetToken(String email) {
	return Jwts.builder()
			       .subject(email)
			       .claim("type", "PASSWORD_RESET")
			       .issuedAt(new Date())
			       .expiration(new Date(System.currentTimeMillis() + PASSWORD_RESET_EXPIRATION))
			       .signWith(getSigningKey())
			       .compact();
}

public String extractEmailFromPasswordResetToken(String token) {
	try {
		return Jwts.parser()
				       .verifyWith(getSigningKey())
				       .build()
				       .parseSignedClaims(token)
				       .getPayload()
				       .getSubject();
	} catch (JwtException e) {
		throw new BadRequestException("Invalid password reset token");
	}
}
}
