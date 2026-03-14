package com.leedTech.studentFeeOneTimePayment.dto.auth;
import jakarta.validation.constraints.NotBlank;
public record VerifyResetOtpCode(
		@NotBlank(message = "optCode is required")
		String otpCode
) {
}
