package com.offerblock.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final String ERROR = "error";
	private static final String STATUS = "status";

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorMessage> handleAuthenticationException(AuthenticationException e) {
		ErrorMessage errorMessage = new ErrorMessage(HttpStatus.UNAUTHORIZED.value(),
				"Full authentication is required to access this resource");
		return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorMessage> handleAccessDeniedException(AccessDeniedException e) {
		ErrorMessage errorMessage = new ErrorMessage(HttpStatus.FORBIDDEN.value(),
				"You do not have permission to access this resource");
		return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorMessage> handleResourceNotFoundException(ResourceNotFoundException ex) {
		ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND.value(), ex.getMessage());
		return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleException(Exception ex) {
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put(ERROR, "An unexpected error occurred: " + ex.getMessage());
		errorResponse.put(STATUS, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleMethodArgumentException(MethodArgumentNotValidException e) {
		Map<String, String> errors = new HashMap<>();
		List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		for (FieldError error : fieldErrors) {
			String fieldName = error.getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
		Map<String, String> errors = new HashMap<>();
		Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
		for (ConstraintViolation<?> violation : violations) {
			String fieldName = violation.getPropertyPath().toString();
			String errorMessage = violation.getMessage();
			errors.put(fieldName, errorMessage);
		}
		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(
			DataIntegrityViolationException ex) {

		Map<String, String> errorResponse = new HashMap<>();

		if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException cause) {

			String constraintName = cause.getConstraintName();
			if (constraintName != null) {
				if (constraintName.contains("unique_phone")) {
					errorResponse.put(ERROR, "Phone number is already in use.");
				} else if (constraintName.contains("unique_email")) {
					errorResponse.put(ERROR, "Email is already in use.");
				} else {
					errorResponse.put(ERROR, "Duplicate entry violates unique constraint.");
				}
			} else {
				errorResponse.put(ERROR, "Data integrity violation occurred.");
			}
		}

		errorResponse.put(STATUS, String.valueOf(HttpStatus.CONFLICT.value()));
		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(DuplicateValueExistsException.class)
	public ResponseEntity<Map<String, String>> handleDuplicateValueException(DuplicateValueExistsException ex) {
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put(ERROR, ex.getMessage());
		errorResponse.put(STATUS, String.valueOf(HttpStatus.CONFLICT.value()));
		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
	}

}
