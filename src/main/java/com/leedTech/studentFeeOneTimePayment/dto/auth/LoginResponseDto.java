package com.leedTech.studentFeeOneTimePayment.dto.auth;
import com.leedTech.studentFeeOneTimePayment.constant.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(name = "LoginResponse", description = "Response payload returned after successful login")
public record LoginResponseDto(
		UUID id,
		String firstName,
		String lastName,
		String email,
		UserRole role,
		boolean accountVerified,
		boolean accountActive,
		String message
) {}