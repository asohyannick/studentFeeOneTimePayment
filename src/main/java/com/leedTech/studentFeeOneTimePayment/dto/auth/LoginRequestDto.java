package com.leedTech.studentFeeOneTimePayment.dto.auth;
import jakarta.validation.constraints.*;
public record LoginRequestDto(
		@NotBlank(message = "Email is required")
		@Email(message = "Email must be valid")
		String email,
		
		@NotBlank(message = "Password is required")
		String password
) {}