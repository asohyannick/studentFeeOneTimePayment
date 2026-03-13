package com.leedTech.studentFeeOneTimePayment.dto.auth;
import jakarta.validation.constraints.*;

public record MagicLinkRequestDto(
		@NotBlank(message = "Email is required")
		@Email(message = "Email must be valid")
		String email
) {}