package com.leedTech.studentFeeOneTimePayment.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.impl.FileCountLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

private ResponseEntity <GlobalExceptionResponseHandler> buildResponse(
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

@ExceptionHandler (NoHandlerFoundException.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleNotFoundException(
		NoHandlerFoundException ex,
		HttpServletRequest request
) {
	return buildResponse(
			"Resource not found: " + ex.getRequestURL(),
			"The requested endpoint does not exist",
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

@ExceptionHandler( HttpMediaTypeNotSupportedException.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleMediaTypeNotSupported(
		HttpMediaTypeNotSupportedException ex,
		HttpServletRequest request
) {
	return buildResponse(
			"Content type '" + ex.getContentType() + "' is not supported",
			"This endpoint requires Content-Type: multipart/form-data",
			HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(),
			HttpStatus.UNSUPPORTED_MEDIA_TYPE,
			request
	);
}

@ExceptionHandler(HttpMessageNotReadableException.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleMessageNotReadable(
		HttpMessageNotReadableException ex,
		HttpServletRequest request
) {
	return buildResponse(
			"Malformed or missing request body",
			"Please provide a valid request payload",
			HttpStatus.BAD_REQUEST.getReasonPhrase(),
			HttpStatus.BAD_REQUEST,
			request
	);
}

@ExceptionHandler( MissingServletRequestParameterException.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleMissingParams(
		MissingServletRequestParameterException ex,
		HttpServletRequest request
) {
	return buildResponse(
			"Required parameter '" + ex.getParameterName() + "' is missing",
			"Please provide all required parameters",
			HttpStatus.BAD_REQUEST.getReasonPhrase(),
			HttpStatus.BAD_REQUEST,
			request
	);
}

@ExceptionHandler( MultipartException.class)
public ResponseEntity<GlobalExceptionResponseHandler> handleMultipartException(
		MultipartException ex,
		HttpServletRequest request
) {
	String message = "Failed to process multipart request";
	
	if (ex.getCause() instanceof FileCountLimitExceededException ) {
		message = "Too many form fields. Please reduce the number of fields in your request";
	} else if (ex.getCause() instanceof FileSizeLimitExceededException ) {
		message = "File size exceeds the maximum allowed limit of 10MB";
	} else if (ex.getCause() instanceof SizeLimitExceededException ) {
		message = "Total request size exceeds the maximum allowed limit of 50MB";
	}
	return buildResponse(
			message,
			"Multipart request processing failed",
			HttpStatus.BAD_REQUEST.getReasonPhrase(),
			HttpStatus.BAD_REQUEST,
			request
	);
}

@ExceptionHandler(RuntimeException.class)
public ResponseEntity< Map <String, Object> > handleRuntimeException(
		RuntimeException ex, HttpServletRequest request) {
	
	int status = ex.getMessage().contains("quota") ? 503 : 500;
	
	return ResponseEntity.status(status).body(Map.of(
			"status",    status,
			"error",     status == 503 ? "Service Unavailable" : "Internal Server Error",
			"message",   ex.getMessage(),
			"path",      request.getRequestURI(),
			"timestamp", Instant.now()
	));
}
}