package com.leedTech.studentFeeOneTimePayment.dto.auth;
import jakarta.validation.constraints.NotBlank;
public record GoogleAuthRequestDto(
		@NotBlank(message = "Firebase ID token is required")
		String idToken
) {}