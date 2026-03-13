package com.leedTech.studentFeeOneTimePayment.dto.auth;
import com.leedTech.studentFeeOneTimePayment.constant.UserRole;
import java.time.Instant;
import java.util.UUID;

public record StudentResponseDto(
		UUID id,
		String firstName,
		String lastName,
		String email,
		UserRole role,
		boolean accountVerified,
		boolean accountActive,
		boolean accountBlocked,
		Instant createdAt,
		Instant updatedAt
) {}