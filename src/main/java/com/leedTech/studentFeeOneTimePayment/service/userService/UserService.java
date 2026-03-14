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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
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

private User createGoogleUser(String email, String firstName, String lastName) {
	User user = new User();
	user.setEmail(email);
	user.setFirstName(firstName);
	user.setLastName(lastName != null ? lastName : "");
	user.setPassword("GOOGLE_AUTH_" + email);
	user.setRole(UserRole.STUDENT);
	user.setAccountVerified(true);
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
	user.setRole (UserRole.STUDENT);
	String otpCode = emailConfigTemplate.generateOtp();
	user.setOtpCode(passwordEncoder.encode(otpCode));
	user.setOtpExpirationDate( Instant.now().plus( Duration.ofMinutes(OTP_EXPIRY_MINUTES)));
	
	userRepository.save(user);
	
	emailConfigTemplate.sendOtpEmail(
			user.getEmail(),
			user.getFirstName(),
			otpCode
	);
	
	log.info("User registered successfully: {}", user.getEmail());
	return userMapper.toRegistrationResponseDto(user);
}

@Transactional
public void verifyOtp(OtpRequestDto request) {
	User user = userRepository.findUsersWithActiveOtp(Instant.now())
			            .stream()
			            .filter(u -> passwordEncoder.matches(request.otpCode(), u.getOtpCode()))
			            .findFirst()
			            .orElseThrow(() -> new BadRequestException("Invalid or expired OTP code"));
	user.setAccountVerified(true);
	user.setAccountActive(true);
	user.setOtpCode(null);
	user.setOtpExpirationDate(null);
	userRepository.save(user);
	log.info("Account verified successfully for: {}", user.getEmail());
}


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

@Transactional
public LoginResponseDto login( LoginRequestDto request, HttpServletResponse response) {
	User user = findByEmail(request.email());
	
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
	
	user.setFailedLoginAttempts(0);
	user.setLockedUntil(null);
	user.setLastLoginAt(Instant.now());
	userRepository.save(user);
	
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

public void logout( HttpServletRequest request, HttpServletResponse response) {
	clearTokenCookies(response);
	
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

@Transactional
public void sendMagicLink(MagicLinkRequestDto request) {
	User user = findByEmail(request.email());
	
	if (!user.isAccountVerified()) {
		throw new BadRequestException("Please verify your account first");
	}
	if (user.isAccountBlocked()) {
		throw new BadRequestException("Your account has been blocked. Contact support");
	}
	
	String token = jwtConfig.generateMagicLinkToken(user.getEmail());
	user.setMagicLinkToken(token);
	user.setMagicLinkExpirationDate(Instant.now().plus(Duration.ofMinutes(MAGIC_LINK_EXPIRY_MINUTES)));
	userRepository.save(user);
	
	emailConfigTemplate.sendMagicLinkEmail(user.getEmail(), user.getFirstName(), token);
	log.info("Magic link sent to: {}", user.getEmail());
}


@Transactional
public LoginResponseDto verifyMagicLink( VerifyMagicLinkToken verifyMagicLinkToken, HttpServletResponse response) {
	User user = userRepository.findByMagicLinkToken(verifyMagicLinkToken.token ())
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


@Transactional
public void resendMagicLink(MagicLinkRequestDto request) {
	sendMagicLink(request);
	log.info("Magic link resent to: {}", request.email());
}


public List <StudentResponseDto> fetchAllStudents() {
	return userRepository.findAllByRole(UserRole.STUDENT)
			       .stream()
			       .map(userMapper::toStudentResponseDto)
			       .toList();
}

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
		FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(idToken);
		
		String email     = firebaseToken.getEmail();
		String firstName = extractFirstName(firebaseToken.getName());
		String lastName  = extractLastName(firebaseToken.getName());
		
		User user = userRepository.findByEmail(email)
				            .orElseGet(() -> createGoogleUser(email, firstName, lastName));
		
		user.setLastLoginAt(Instant.now());
		userRepository.save(user);
		
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

@Transactional
public void blockStudent(UUID studentId) {
	User student = userRepository.findById(studentId)
			               .orElseThrow(() -> new NotFoundException(
					               "Student not found with id: " + studentId
			               ));
	
	if (student.getRole() != UserRole.STUDENT) {
		throw new BadRequestException("User is not a student");
	}
	if (student.isAccountBlocked()) {
		throw new BadRequestException("Student account is already blocked");
	}
	
	student.setAccountBlocked(true);
	student.setAccountActive(false);
	userRepository.save(student);
	
	log.info("Student account blocked: {}", student.getEmail());
}


@Transactional
public void unblockStudent(UUID studentId) {
	User student = userRepository.findById(studentId)
			               .orElseThrow(() -> new NotFoundException(
					               "Student not found with id: " + studentId
			               ));
	
	if (student.getRole() != UserRole.STUDENT) {
		throw new BadRequestException("User is not a student");
	}
	if (!student.isAccountBlocked()) {
		throw new BadRequestException("Student account is not blocked");
	}
	
	student.setAccountBlocked(false);
	student.setAccountActive(true);
	userRepository.save(student);
	log.info("Student account unblocked: {}", student.getEmail());
}

@Transactional
public void forgotPassword(ForgotPasswordRequestDto request) {
	User user = userRepository.findByEmail(request.email())
			            .orElseThrow(() -> new NotFoundException("No account found with this email"));
	
	if (!user.isAccountVerified()) {
		throw new BadRequestException("Account is not verified. Please verify your account first");
	}
	
	String rawOtp = emailConfigTemplate.generateOtp();
	String hashedOtp = passwordEncoder.encode(rawOtp);
	
	user.setOtpCode(hashedOtp);
	user.setOtpExpirationDate(Instant.now().plus(5, ChronoUnit.MINUTES));
	userRepository.save(user);
	
	emailConfigTemplate.sendPasswordResetOtpEmail(
			user.getEmail(),
			user.getFirstName(),
			rawOtp
	);
	
	log.info("Password reset OTP sent to: {}", user.getEmail());
}

@Transactional
public Map <String, String> verifyResetOtp( VerifyResetOtpCode request) {
	
	List<User> usersWithOtp = userRepository.findUsersWithActiveOtp(Instant.now());
	
	User matchedUser = usersWithOtp.stream()
			                   .filter(user -> passwordEncoder.matches(request.otpCode(), user.getOtpCode()))
			                   .findFirst()
			                   .orElseThrow(() -> new BadRequestException("Invalid OTP code"));
	
	if (Instant.now().isAfter(matchedUser.getOtpExpirationDate())) {
		throw new BadRequestException("OTP has expired. Please request a new one");
	}
	
	matchedUser.setOtpCode(null);
	matchedUser.setOtpExpirationDate(null);
	
	userRepository.save(matchedUser);
	
	String resetToken = jwtConfig.generatePasswordResetToken(matchedUser.getEmail());
	
	log.info("Reset OTP verified for: {}", matchedUser.getEmail());
	
	return Map.of("resetToken", resetToken);
}

@Transactional
public void resetPassword(String token, ResetPasswordRequestDto request) {

	if (!jwtConfig.isPasswordResetToken(token)) {
		throw new BadRequestException("Invalid reset token");
	}
	
	String email = jwtConfig.extractEmailFromPasswordResetToken(token);
	
	User user = userRepository.findByEmail(email)
			            .orElseThrow(() -> new NotFoundException("User not found"));
	
	if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
		throw new BadRequestException("New password must be different from the old password");
	}
	
	user.setPassword(passwordEncoder.encode(request.newPassword()));
	userRepository.save(user);
	
	log.info("Password reset successfully for: {}", user.getEmail());
}
}
