package com.leedTech.studentFeeOneTimePayment.dto.auth;
import jakarta.validation.constraints.NotBlank;
public record VerifyMagicLinkToken(
		@NotBlank(message = "Token must be provided")
		String token
) { }
