package com.leedTech.studentFeeOneTimePayment.service.userService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.leedTech.studentFeeOneTimePayment.config.emailTemplateConfig.EmailConfigTemplate;
import com.leedTech.studentFeeOneTimePayment.config.jwtConfig.JWTAuthenticationFilter;
import com.leedTech.studentFeeOneTimePayment.config.jwtConfig.JWTConfig;
import com.leedTech.studentFeeOneTimePayment.constant.UserRole;
import com.leedTech.studentFeeOneTimePayment.dto.auth.*;
import com.leedTech.studentFeeOneTimePayment.entity.user.User;
import com.leedTech.studentFeeOneTimePayment.exception.BadRequestException;
import com.leedTech.studentFeeOneTimePayment.exception.NotFoundException;
import com.leedTech.studentFeeOneTimePayment.mapper.userMapper.UserMapper;
import com.leedTech.studentFeeOneTimePayment.repository.userRepository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
private final UserRepository userRepository;
private final JWTAuthenticationFilter jwtAuthenticationFilter;
private final BCryptPasswordEncoder passwordEncoder;
private final UserMapper userMapper;
private final JWTConfig jwtConfig;
private final EmailConfigTemplate emailConfigTemplate;
private final RedisTemplate <String, String> redisTemplate;
private final FirebaseAuth firebaseAuth;

private static final long   OTP_EXPIRY_MINUTES          = 5;
private static final long   MAGIC_LINK_EXPIRY_MINUTES   = 15;
private static final int    MAX_FAILED_ATTEMPTS         = 5;
private static final long   LOCK_DURATION_MINUTES       = 30;
private static final String OTP_PREFIX                  = "OTP:";
private static final String ACCESS_TOKEN_COOKIE         = "accessToken";
private static final String REFRESH_TOKEN_COOKIE        = "refreshToken";

// ─── Private Helpers ─────────────────────────────────────────────

private User findByEmail(String email) {
	return userRepository.findByEmail(email)
			       .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
}

private void handleFailedLogin(User user) {
	int attempts = user.getFailedLoginAttempts() + 1;
	user.setFailedLoginAttempts(attempts);
	
	if (attempts >= MAX_FAILED_ATTEMPTS) {
		user.setLockedUntil(Instant.now().plus(Duration.ofMinutes(LOCK_DURATION_MINUTES)));
		log.warn("Account locked due to too many failed attempts: {}", user.getEmail());
	}
	userRepository.save(user);
}

private void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
	Cookie accessCookie = new Cookie(ACCESS_TOKEN_COOKIE, accessToken);
	accessCookie.setHttpOnly(true);
	accessCookie.setSecure(true);
	accessCookie.setPath("/");
	accessCookie.setMaxAge((int) jwtConfig.getAccessTokenExpirationSeconds());
	
	Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken);
	refreshCookie.setHttpOnly(true);
	refreshCookie.setSecure(true);
	refreshCookie.setPath("/");
	refreshCookie.setMaxAge((int) jwtConfig.getRefreshTokenExpirationSeconds());
	
	response.addCookie(accessCookie);
	response.addCookie(refreshCookie);
}

private void clearTokenCookies(HttpServletResponse response) {
	Cookie accessCookie = new Cookie(ACCESS_TOKEN_COOKIE, null);
	accessCookie.setHttpOnly(true);
	accessCookie.setSecure(true);
	accessCookie.setPath("/");
	accessCookie.setMaxAge(0);
	
	Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE, null);
	refreshCookie.setHttpOnly(true);
	refreshCookie.setSecure(true);
	refreshCookie.setPath("/");
	refreshCookie.setMaxAge(0);
	
	response.addCookie(accessCookie);
	response.addCookie(refreshCookie);
}

private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
	if (request.getCookies() == null) return null;
	for ( Cookie cookie : request.getCookies()) {
		if (cookieName.equals(cookie.getName())) return cookie.getValue();
	}
	return null;
}

// ─── Name Helpers ────────────────────────────────────────────────

private String extractFirstName(String fullName) {
	if (fullName == null || fullName.isBlank()) return "Google";
	String[] parts = fullName.trim().split(" ");
	return parts[0];
}

private String extractLastName(String fullName) {
	if (fullName == null || fullName.isBlank()) return "User";
	String[] parts = fullName.trim().split(" ", 2);
	return parts.length > 1 ? parts[1] : "";
}

// ─── Create Google User ──────────────────────────────────────────

private User createGoogleUser(String email, String firstName, String lastName) {
	User user = new User();
	user.setEmail(email);
	user.setFirstName(firstName);
	user.setLastName(lastName != null ? lastName : "");
	user.setPassword("GOOGLE_AUTH_" + email); // placeholder, never used
	user.setRole(UserRole.STUDENT);
	user.setAccountVerified(true);  // Google accounts are pre-verified
	user.setAccountActive(true);
	user.setAccountBlocked(false);
	user.setFailedLoginAttempts(0);
	
	log.info("New Google user created: {}", email);
	return userRepository.save(user);
}


@Transactional
public RegistrationResponseDto register( RegistrationRequestDto request) {
	if (userRepository.existsByEmail(request.email())) {
		throw new BadRequestException("Email already in use");
	}
	if (!request.password().equals(request.confirmPassword())) {
		throw new BadRequestException("Passwords do not match");
	}
	
	User user = userMapper.toEntity(request);
	user.setPassword(passwordEncoder.encode(request.password()));
	
	// Generate and store OTP
	String otpCode = emailConfigTemplate.generateOtp();
	user.setOtpCode(passwordEncoder.encode(otpCode));
	user.setOtpExpirationDate( Instant.now().plus( Duration.ofMinutes(OTP_EXPIRY_MINUTES)));
	
	userRepository.save(user);
	
	// Send OTP email
	emailConfigTemplate.sendOtpEmail(
			user.getEmail(),
			user.getFirstName(),
			otpCode
	);
	
	log.info("User registered successfully: {}", user.getEmail());
	return userMapper.toRegistrationResponseDto(user);
}

// ─── Verify OTP ──────────────────────────────────────────────────

@Transactional
public void verifyOtp( OtpRequestDto request) {
	User user = findByEmail(request.email());
	
	if (user.getOtpCode() == null || user.getOtpExpirationDate() == null) {
		throw new BadRequestException("No OTP was requested for this account");
	}
	if (Instant.now().isAfter(user.getOtpExpirationDate())) {
		throw new BadRequestException("OTP has expired. Please request a new one");
	}
	if (!passwordEncoder.matches(request.otpCode(), user.getOtpCode())) {
		throw new BadRequestException("Invalid OTP code");
	}
	
	user.setAccountVerified(true);
	user.setAccountActive(true);
	user.setOtpCode(null);
	user.setOtpExpirationDate(null);
	userRepository.save(user);
	
	log.info("Account verified successfully for: {}", user.getEmail());
}

// ─── Resend OTP ──────────────────────────────────────────────────

@Transactional
public void resendOtp( MagicLinkRequestDto request) {
	User user = findByEmail(request.email());
	
	if (user.isAccountVerified()) {
		throw new BadRequestException("Account is already verified");
	}
	
	String otpCode = emailConfigTemplate.generateOtp();
	user.setOtpCode(passwordEncoder.encode(otpCode));
	user.setOtpExpirationDate(Instant.now().plus(Duration.ofMinutes(OTP_EXPIRY_MINUTES)));
	userRepository.save(user);
	
	emailConfigTemplate.sendOtpEmail(user.getEmail(), user.getFirstName(), otpCode);
	log.info("OTP resent to: {}", user.getEmail());
}

// ─── Login ───────────────────────────────────────────────────────

@Transactional
public LoginResponseDto login( LoginRequestDto request, HttpServletResponse response) {
	User user = findByEmail(request.email());
	
	// Check if account is locked
	if (user.getLockedUntil() != null && Instant.now().isBefore(user.getLockedUntil())) {
		throw new BadRequestException("Account is locked. Try again after " + user.getLockedUntil());
	}
	if (!user.isAccountVerified()) {
		throw new BadRequestException("Please verify your account before logging in");
	}
	if (user.isAccountBlocked()) {
		throw new BadRequestException("Your account has been blocked. Contact support");
	}
	if (!passwordEncoder.matches(request.password(), user.getPassword())) {
		handleFailedLogin(user);
		throw new BadRequestException("Invalid email or password");
	}
	
	// Reset failed attempts on success
	user.setFailedLoginAttempts(0);
	user.setLockedUntil(null);
	user.setLastLoginAt(Instant.now());
	userRepository.save(user);
	
	// Generate tokens and set cookies
	String accessToken  = jwtConfig.generateAccessToken(user.getEmail(), user.getRole().name());
	String refreshToken = jwtConfig.generateRefreshToken(user.getEmail());
	setTokenCookies(response, accessToken, refreshToken);
	
	log.info("User logged in successfully: {}", user.getEmail());
	return new LoginResponseDto(
			user.getId(),
			user.getFirstName(),
			user.getLastName(),
			user.getEmail(),
			user.getRole(),
			user.isAccountVerified(),
			user.isAccountActive(),
			"Login successful"
	);
}

// ─── Logout ──────────────────────────────────────────────────────

public void logout( HttpServletRequest request, HttpServletResponse response) {
	clearTokenCookies(response);
	
	// Blacklist access token in Redis
	String accessToken = extractTokenFromCookie(request, ACCESS_TOKEN_COOKIE);
	if (accessToken != null) {
		long expirySeconds = jwtConfig.getAccessTokenExpirationSeconds();
		redisTemplate.opsForValue().set(
				"BLACKLIST:" + accessToken,
				"true",
				Duration.ofSeconds(expirySeconds)
		);
	}
	log.info("User logged out successfully");
}

// ─── Login via Magic Link ────────────────────────────────────────

@Transactional
public void sendMagicLink(MagicLinkRequestDto request) {
	User user = findByEmail(request.email());
	
	if (!user.isAccountVerified()) {
		throw new BadRequestException("Please verify your account first");
	}
	if (user.isAccountBlocked()) {
		throw new BadRequestException("Your account has been blocked. Contact support");
	}
	
	String token = UUID.randomUUID().toString();
	user.setMagicLinkToken(token);
	user.setMagicLinkExpirationDate(Instant.now().plus(Duration.ofMinutes(MAGIC_LINK_EXPIRY_MINUTES)));
	userRepository.save(user);
	
	emailConfigTemplate.sendMagicLinkEmail(user.getEmail(), user.getFirstName(), token);
	log.info("Magic link sent to: {}", user.getEmail());
}

// ─── Verify Magic Link ───────────────────────────────────────────

@Transactional
public LoginResponseDto verifyMagicLink(String token, HttpServletResponse response) {
	User user = userRepository.findByMagicLinkToken(token)
			            .orElseThrow(() -> new BadRequestException("Invalid or expired magic link"));
	
	if (Instant.now().isAfter(user.getMagicLinkExpirationDate())) {
		throw new BadRequestException("Magic link has expired. Please request a new one");
	}
	
	user.setMagicLinkToken(null);
	user.setMagicLinkExpirationDate(null);
	user.setLastLoginAt(Instant.now());
	userRepository.save(user);
	
	String accessToken  = jwtConfig.generateAccessToken(user.getEmail(), user.getRole().name());
	String refreshToken = jwtConfig.generateRefreshToken(user.getEmail());
	setTokenCookies(response, accessToken, refreshToken);
	
	log.info("Magic link login successful for: {}", user.getEmail());
	return new LoginResponseDto(
			user.getId(),
			user.getFirstName(),
			user.getLastName(),
			user.getEmail(),
			user.getRole(),
			user.isAccountVerified(),
			user.isAccountActive(),
			"Login successful via magic link"
	);
}

// ─── Resend Magic Link ───────────────────────────────────────────

@Transactional
public void resendMagicLink(MagicLinkRequestDto request) {
	sendMagicLink(request);
	log.info("Magic link resent to: {}", request.email());
}

// ─── Fetch All Students ──────────────────────────────────────────

public List <StudentResponseDto> fetchAllStudents() {
	return userRepository.findAllByRole( UserRole.STUDENT)
			       .stream()
			       .map(userMapper::toStudentResponseDto)
			       .toList();
}

// ─── Delete Student ──────────────────────────────────────────────

@Transactional
public void deleteStudent(UUID studentId) {
	User user = userRepository.findById(studentId)
			            .orElseThrow(() -> new NotFoundException("Student not found with id: " + studentId));
	
	if (user.getRole() != UserRole.STUDENT) {
		throw new BadRequestException("User is not a student");
	}
	
	userRepository.delete(user);
	log.info("Student deleted successfully: {}", studentId);
}

public long countTotalStudents() {
	return userRepository.findAllByRole(UserRole.STUDENT).size();
}

@Transactional
public LoginResponseDto loginWithGoogle(String idToken, HttpServletResponse response) {
	try {
		// Verify Firebase ID token
		FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(idToken);
		
		String email     = firebaseToken.getEmail();
		String firstName = extractFirstName(firebaseToken.getName());
		String lastName  = extractLastName(firebaseToken.getName());
		
		// Find or create user
		User user = userRepository.findByEmail(email)
				            .orElseGet(() -> createGoogleUser(email, firstName, lastName));
		
		// Update last login
		user.setLastLoginAt(Instant.now());
		userRepository.save(user);
		
		// Generate tokens and set cookies
		String accessToken  = jwtConfig.generateAccessToken(user.getEmail(), user.getRole().name());
		String refreshToken = jwtConfig.generateRefreshToken(user.getEmail());
		setTokenCookies(response, accessToken, refreshToken);
		
		log.info("Google login successful for: {}", email);
		
		return new LoginResponseDto(
				user.getId(),
				user.getFirstName(),
				user.getLastName(),
				user.getEmail(),
				user.getRole(),
				user.isAccountVerified(),
				user.isAccountActive(),
				"Google login successful. Welcome, " + user.getFirstName() + "!"
		);
		
	} catch (Exception e) {
		log.error("Google login failed: {}", e.getMessage());
		throw new BadRequestException("Invalid Google token: " + e.getMessage());
	}
}

}
