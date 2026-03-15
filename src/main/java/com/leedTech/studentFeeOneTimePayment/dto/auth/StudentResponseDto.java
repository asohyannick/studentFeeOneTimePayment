package com.leedTech.studentFeeOneTimePayment.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.leedTech.studentFeeOneTimePayment.constant.UserRole;

import java.time.Instant;
import java.util.UUID;
@JsonInclude (JsonInclude.Include.NON_NULL)
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