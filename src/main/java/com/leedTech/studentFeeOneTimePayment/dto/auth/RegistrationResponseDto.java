package com.leedTech.studentFeeOneTimePayment.dto.auth;
import com.leedTech.studentFeeOneTimePayment.constant.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(
		name = "RegistrationResponse",
		description = "Response payload returned after a successful user registration"
)
public record RegistrationResponseDto(
		
		@Schema(
				description = "Unique identifier of the newly created user",
				example = "a3f1c2d4-12ab-4e56-89cd-abcdef012345"
		)
		UUID id,
		
		@Schema(
				description = "First name of the registered user",
				example = "John"
		)
		String firstName,
		
		@Schema(
				description = "Last name of the registered user",
				example = "Doe"
		)
		String lastName,
		
		@Schema(
				description = "Email address of the registered user",
				example = "john.doe@example.com"
		)
		String email,
		
		@Schema(
				description = "Role assigned to the user in the school management system",
				example = "STUDENT"
		)
		UserRole role,
		
		@Schema(
				description = "Indicates whether the user's email has been verified",
				example = "false"
		)
		boolean accountVerified,
		
		@Schema(
				description = "Indicates whether the user account is currently active",
				example = "false"
		)
		boolean accountActive,
		
		@Schema(
				description = "Timestamp when the account was created",
				example = "2026-03-10T22:25:07.130Z"
		)
		Instant createdAt,
		
		@Schema(
				description = "Human-readable message describing the result of the registration",
				example = "Registration successful. Please check your email to verify your account."
		)
		String message

) {}