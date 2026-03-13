package com.leedTech.studentFeeOneTimePayment.exception;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
@Schema(name = "ErrorResponse", description = "Standard error response structure")
public record GlobalExceptionResponseHandler(
		
		@Schema(description = "Time the error occurred", example = "2026-03-10T22:25:07.130Z")
		Instant timestamp,
		
		@Schema(description = "Short error message", example = "Access denied")
		String message,
		
		@Schema(description = "Detailed error description", example = "You do not have permission to access this resource")
		String details,
		
		@Schema(description = "HTTP status code", example = "403")
		int status,
		
		@Schema(description = "HTTP status name", example = "FORBIDDEN")
		String statusCode,
		
		@Schema(description = "Application error code", example = "FORBIDDEN")
		String errorCode,
		
		@Schema(description = "Request path", example = "/api/v1/payments")
		String path,
		
		@Schema(description = "HTTP method", example = "GET")
		String method

) {}