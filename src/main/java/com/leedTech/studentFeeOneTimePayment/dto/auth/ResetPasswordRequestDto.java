package com.leedTech.studentFeeOneTimePayment.dto.auth;
import jakarta.validation.constraints.*;
public record ResetPasswordRequestDto(
		@NotBlank(message = "New password is required")
		@Size(min = 8, message = "Password must be at least 8 characters")
		@Pattern(
				regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
				message = "Password must contain uppercase, lowercase, number and special character"
		)
		String newPassword,
		
		@NotBlank(message = "Confirm password is required")
		String confirmPassword
) {}