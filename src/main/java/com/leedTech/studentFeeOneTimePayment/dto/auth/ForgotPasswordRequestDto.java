package com.leedTech.studentFeeOneTimePayment.dto.auth;
import jakarta.validation.constraints.*;
public record ForgotPasswordRequestDto(
		@NotBlank(message = "Email is required")
		@Email(message = "Invalid email format")
		String email
) {}