package com.leedTech.studentFeeOneTimePayment.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.time.Instant;
@RestControllerAdvice
public class GlobalExceptionHandler {
private ResponseEntity<GlobalExceptionResponseHandler> buildResponse(
		String message,
		String details,
		String statusCode,
		HttpStatus status,
		HttpServletRequest request
) {
	
	GlobalExceptionResponseHandler response =
			new GlobalExceptionResponseHandler(
					Instant.now(),
					message,
					details,
					status.value(),
					statusCode,
					request.getRequestURI(),
					request.getMethod()
			);
	
	return ResponseEntity.status(status).body(response);
}


@ExceptionHandler (NotFoundException.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleNotFoundException(
		NoHandlerFoundException ex,
		HttpServletRequest request
) {
	return buildResponse(
			ex.getMessage(),
			"Resource not found!",
			HttpStatus.NOT_FOUND.getReasonPhrase(),
			HttpStatus.NOT_FOUND,
			request
	);
}

@ExceptionHandler( MethodArgumentNotValidException.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleValidationException(
		MethodArgumentNotValidException ex,
		HttpServletRequest request
) {
	String errorMessage = ex.getBindingResult()
			                      .getFieldErrors()
			                      .stream()
			                      .map(error -> error.getField() + ": " + error.getDefaultMessage())
			                      .findFirst()
			                      .orElse("Validation failed");
	
	return buildResponse(
			errorMessage,
			"Invalid request payload",
			HttpStatus.BAD_REQUEST.getReasonPhrase(),
			HttpStatus.BAD_REQUEST,
			request
	);
}

@ExceptionHandler(UnAuthorizedException.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleUnauthorizedException(
		UnAuthorizedException ex,
		HttpServletRequest request
) {
	return buildResponse(
			ex.getMessage(),
			"Authentication failed",
			HttpStatus.UNAUTHORIZED.getReasonPhrase(),
			HttpStatus.UNAUTHORIZED,
			request
	);
}

@ExceptionHandler(ForbiddenException.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleAccessDeniedException(
		ForbiddenException ex,
		HttpServletRequest request
) {
	return buildResponse(
			ex.getMessage(),
			"Access denied",
			HttpStatus.FORBIDDEN.getReasonPhrase(),
			HttpStatus.FORBIDDEN,
			request
	);
}

@ExceptionHandler(ConflictRequestException.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleConflictException(
		ConflictRequestException ex,
		HttpServletRequest request
) {
	return buildResponse(
			ex.getMessage(),
			"Conflict occurred",
			HttpStatus.CONFLICT.getReasonPhrase(),
			HttpStatus.CONFLICT,
			request
	);
}

@ExceptionHandler(InternalServerErrorException.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleGlobalException(
		InternalServerErrorException ex,
		HttpServletRequest request
) {
	return buildResponse(
			ex.getMessage(),
			"An unexpected error occurred",
			HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
			HttpStatus.INTERNAL_SERVER_ERROR,
			request
	);
}
}
