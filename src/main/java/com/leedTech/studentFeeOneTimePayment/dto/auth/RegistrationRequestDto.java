package com.leedTech.studentFeeOneTimePayment.dto.auth;

import com.leedTech.studentFeeOneTimePayment.constant.UserRole;
import jakarta.validation.constraints.*;

public record RegistrationRequestDto(
		
		@NotBlank(message = "First name is required")
		@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
		String firstName,
		
		@NotBlank(message = "Last name is required")
		@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
		String lastName,
		
		@NotBlank(message = "Email is required")
		@Email(message = "Email must be valid")
		String email,
		
		@NotBlank(message = "Password is required")
		@Size(min = 8, message = "Password must be at least 8 characters")
		@Pattern(
				regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
				message = "Password must contain uppercase, lowercase, number and special character"
		)
		String password,
		
		@NotBlank(message = "Confirm password is required")
		String confirmPassword,
		
		@NotNull(message = "Role is required")
		UserRole role

) {}