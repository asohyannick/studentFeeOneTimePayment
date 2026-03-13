package com.leedTech.studentFeeOneTimePayment.dto.auth;
import jakarta.validation.constraints.*;
public record OtpRequestDto(
		@NotBlank(message = "OTP code is required")
		@Size(min = 6, max = 6, message = "OTP must be 6 digits")
		String otpCode
) {}