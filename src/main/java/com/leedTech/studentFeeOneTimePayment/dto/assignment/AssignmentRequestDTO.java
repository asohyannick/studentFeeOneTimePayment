package com.leedTech.studentFeeOneTimePayment.dto.assignment;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AssignmentRequestDTO(
		
		@NotNull(message = "Teacher ID is required")
		UUID teacherId,
		
		@NotNull(message = "Student ID is required")
		UUID studentId,
		
		@NotBlank(message = "Name is required")
		@Size(max = 255)
		String name,
		
		String instructions,
		
		Instant availableFrom,
		
		@Future(message = "Deadline must be in the future")
		Instant deadline,
		
		@Positive(message = "Total points must be positive")
		Double totalPoints,
		
		@DecimalMin(value = "0.0", inclusive = true)
		BigDecimal passingScore,
		
		@Min(value = 1, message = "Max attempts must be at least 1")
		Integer maxAttempts,
		
		String category,
		Boolean isGroupWork,
		String attachmentUrl,
		String gradingType
) {
public AssignmentRequestDTO {
	if (maxAttempts == null) maxAttempts = 1;
}
}
