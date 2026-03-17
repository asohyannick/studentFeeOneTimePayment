package com.leedTech.studentFeeOneTimePayment.dto.assignment;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AssignmentResponseDTO(
		
		UUID id,
		UUID teacherId,
		String teacherName,
		UUID studentId,
		String studentName,
		
		String name,
		String instructions,
		Instant availableFrom,
		Instant deadline,
		Double totalPoints,
		BigDecimal passingScore,
		Integer maxAttempts,
		String category,
		Boolean isGroupWork,
		String attachmentUrl,
		String gradingType,
		
		Instant createdAt,
		Instant updatedAt
) {}