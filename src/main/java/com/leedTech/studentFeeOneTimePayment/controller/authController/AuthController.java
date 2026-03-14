package com.leedTech.studentFeeOneTimePayment.controller.authController;

import com.leedTech.studentFeeOneTimePayment.config.customResponseMessage.CustomResponseMessage;
import com.leedTech.studentFeeOneTimePayment.dto.auth.*;
import com.leedTech.studentFeeOneTimePayment.service.userService.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/api/${api.version}/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication and Authorization API Endpoints", description = "Endpoints for user registration, login, OTP verification, magic link and student management")
public class AuthController {
private final UserService userService;

// ─── Register ────────────────────────────────────────────────────

@Operation(
		summary = "Register a new user",
		description = "Registers a new user and sends an OTP to their email for verification"
)
@ApiResponses({
		@ApiResponse(responseCode = "201", description = "User registered successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid input or email already in use")
})
@PostMapping("/register")
public ResponseEntity<CustomResponseMessage<RegistrationResponseDto>> register(
		@Valid @RequestBody RegistrationRequestDto request
) {
	RegistrationResponseDto data = userService.register(request);
	return ResponseEntity.status(HttpStatus.CREATED).body(
			new CustomResponseMessage<>(
					"Registration successful. Please check your email for your OTP verification code.",
					HttpStatus.CREATED.value(),
					data
			)
	);
}

// ─── Verify OTP ──────────────────────────────────────────────────

@Operation(
		summary = "Verify OTP",
		description = "Verifies the OTP code sent to the user's email to activate their account"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Account verified successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid or expired OTP")
})
@PostMapping("/verify-otp")
public ResponseEntity<CustomResponseMessage<Void>> verifyOtp(
		@Valid @RequestBody OtpRequestDto request
) {
	userService.verifyOtp(request);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					"Account verified successfully. You can now log in.",
					HttpStatus.OK.value(),
					null
			)
	);
}

// ─── Resend OTP ──────────────────────────────────────────────────

@Operation(
		summary = "Resend OTP",
		description = "Resends a new OTP code to the user's email address"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OTP resent successfully"),
		@ApiResponse(responseCode = "400", description = "Account already verified or user not found")
})
@PostMapping("/resend-otp")
public ResponseEntity<CustomResponseMessage<Void>> resendOtp(
		@Valid @RequestBody MagicLinkRequestDto request
) {
	userService.resendOtp(request);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					"A new OTP has been sent to your email address.",
					HttpStatus.OK.value(),
					null
			)
	);
}

// ─── Login ───────────────────────────────────────────────────────

@Operation(
		summary = "Login",
		description = "Authenticates a user and sets access and refresh token cookies in the browser"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Login successful"),
		@ApiResponse(responseCode = "400", description = "Invalid credentials or account not verified"),
		@ApiResponse(responseCode = "403", description = "Account blocked or locked")
})
@PostMapping("/login")
public ResponseEntity<CustomResponseMessage<LoginResponseDto>> login(
		@Valid @RequestBody LoginRequestDto request,
		HttpServletResponse response
) {
	LoginResponseDto data = userService.login(request, response);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					"Login successful. Welcome back, " + data.firstName() + "!",
					HttpStatus.OK.value(),
					data
			)
	);
}

// ─── Logout ──────────────────────────────────────────────────────

@Operation(
		summary = "Logout",
		description = "Logs the user out by clearing token cookies and blacklisting the access token"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Logout successful"),
		@ApiResponse(responseCode = "401", description = "Unauthorized")
})
@PostMapping("/logout")
public ResponseEntity<CustomResponseMessage<Void>> logout(
		HttpServletRequest request,
		HttpServletResponse response
) {
	userService.logout(request, response);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					"You have been logged out successfully.",
					HttpStatus.OK.value(),
					null
			)
	);
}

// ─── Send Magic Link ─────────────────────────────────────────────

@Operation(
		summary = "Send magic link",
		description = "Sends a magic link to the user's email for passwordless login"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Magic link sent successfully"),
		@ApiResponse(responseCode = "400", description = "Account not verified or blocked")
})
@PostMapping("/magic-link/send")
public ResponseEntity<CustomResponseMessage<Void>> sendMagicLink(
		@Valid @RequestBody MagicLinkRequestDto request
) {
	userService.sendMagicLink(request);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					"Magic link has been sent to your email address. It expires in 15 minutes.",
					HttpStatus.OK.value(),
					null
			)
	);
}

// ─── Verify Magic Link ───────────────────────────────────────────

@Operation(
		summary = "Verify magic link",
		description = "Verifies the magic link token and logs the user in by setting token cookies"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Magic link verified and login successful"),
		@ApiResponse(responseCode = "400", description = "Invalid or expired magic link")
})
@PostMapping("/magic-link/verify")
public ResponseEntity<CustomResponseMessage<LoginResponseDto>> verifyMagicLink(
		@RequestParam VerifyMagicLinkToken token,
		HttpServletResponse response
) {
	LoginResponseDto data = userService.verifyMagicLink(token, response);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					"Magic link verified. Welcome back, " + data.firstName() + "!",
					HttpStatus.OK.value(),
					data
			)
	);
}

// ─── Resend Magic Link ───────────────────────────────────────────

@Operation(
		summary = "Resend magic link",
		description = "Resends a new magic link to the user's email address"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Magic link resent successfully"),
		@ApiResponse(responseCode = "400", description = "Account not verified or blocked")
})
@PostMapping("/magic-link/resend")
public ResponseEntity<CustomResponseMessage<Void>> resendMagicLink(
		@Valid @RequestBody MagicLinkRequestDto request
) {
	userService.resendMagicLink(request);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					"A new magic link has been sent to your email address.",
					HttpStatus.OK.value(),
					null
			)
	);
}

// ─── Fetch All Students ──────────────────────────────────────────

@Operation(
		summary = "Fetch all students",
		description = "Returns a list of all registered students. Accessible by ADMIN only"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Students fetched successfully"),
		@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
})
@GetMapping("/students")
public ResponseEntity<CustomResponseMessage<List<StudentResponseDto>>> fetchAllStudents() {
	List<StudentResponseDto> students = userService.fetchAllStudents();
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					students.isEmpty()
							? "No students found."
							: students.size() + " student(s) retrieved successfully.",
					HttpStatus.OK.value(),
					students
			)
	);
}

// ─── Delete Student ──────────────────────────────────────────────

@Operation(
		summary = "Delete a student",
		description = "Permanently deletes a student by their ID. Accessible by ADMIN only"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Student deleted successfully"),
		@ApiResponse(responseCode = "400", description = "User is not a student"),
		@ApiResponse(responseCode = "404", description = "Student not found"),
		@ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
})
@DeleteMapping("/students/{studentId}")
public ResponseEntity<CustomResponseMessage<Void>> deleteStudent(
		@PathVariable UUID studentId
) {
	userService.deleteStudent(studentId);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					"Student deleted successfully.",
					HttpStatus.OK.value(),
					null
			)
	);
}

@Operation(
		summary = "Count total students",
		description = "Returns the total number of registered students. Accessible by ADMIN only"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Student count retrieved successfully"),
		@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
})
@GetMapping("/students/count")
public ResponseEntity<CustomResponseMessage<Long>> countTotalStudents() {
	long count = userService.countTotalStudents();
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					"Total number of students retrieved successfully.",
					HttpStatus.OK.value(),
					count
			)
	);
}

@Operation(
		summary = "Login with Google",
		description = "Authenticates a user via Firebase Google sign-in and sets token cookies"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Google login successful"),
		@ApiResponse(responseCode = "400", description = "Invalid Firebase token"),
		@ApiResponse(responseCode = "401", description = "Unauthorized")
})
@PostMapping("/google-login")
public ResponseEntity<CustomResponseMessage<LoginResponseDto>> googleLogin(
		@Valid @RequestBody GoogleAuthRequestDto request,
		HttpServletResponse response
) {
	LoginResponseDto data = userService.loginWithGoogle(request.idToken(), response);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					data.message(),
					HttpStatus.OK.value(),
					data
			)
	);
}

@Operation(
		summary = "Block a student account",
		description = "Blocks a student account preventing them from logging in"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Student blocked successfully"),
		@ApiResponse(responseCode = "400", description = "Student already blocked or user is not a student"),
		@ApiResponse(responseCode = "404", description = "Student not found"),
		@ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
})
@PatchMapping("/students/{studentId}/block")
public ResponseEntity<CustomResponseMessage<Void>> blockStudent(
		@PathVariable UUID studentId
) {
	userService.blockStudent(studentId);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					"Student account blocked successfully.",
					HttpStatus.OK.value(),
					null
			)
	);
}

@Operation(
		summary = "Unblock a student account",
		description = "Unblocks a previously blocked student account restoring their access"
)
@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Student unblocked successfully"),
		@ApiResponse(responseCode = "400", description = "Student is not blocked or user is not a student"),
		@ApiResponse(responseCode = "404", description = "Student not found"),
		@ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
})
@PatchMapping("/students/{studentId}/unblock")
public ResponseEntity<CustomResponseMessage<Void>> unblockStudent(
		@PathVariable UUID studentId
) {
	userService.unblockStudent(studentId);
	return ResponseEntity.ok(
			new CustomResponseMessage<>(
					"Student account unblocked successfully.",
					HttpStatus.OK.value(),
					null
			)
	);
}

@PostMapping("/forgot-password")
public ResponseEntity< Map <String, Object> > forgotPassword(
		@Valid @RequestBody ForgotPasswordRequestDto request) {
	
	userService.forgotPassword(request);
	
	return ResponseEntity.ok(Map.of(
			"message", "Password reset OTP sent. Please check your email.",
			"statusCode", 200
	));
}

@PostMapping("/verify-reset-otp")
public ResponseEntity<Map<String, String>> verifyResetOtp(
		@RequestBody VerifyResetOtpCode verifyResetOtpCode) {
	
	Map<String, String> response = userService.verifyResetOtp(verifyResetOtpCode);
	
	return ResponseEntity.ok(response);
}

@PostMapping("/reset-password")
public ResponseEntity<Map<String, Object>> resetPassword(
		@RequestHeader("Authorization") String token,
		@Valid @RequestBody ResetPasswordRequestDto request) {
	
	userService.resetPassword(token.replace("Bearer ", ""), request);
	
	return ResponseEntity.ok(Map.of(
			"message", "Password reset successfully. You can now login with your new password.",
			"statusCode", 200
	));
}

}