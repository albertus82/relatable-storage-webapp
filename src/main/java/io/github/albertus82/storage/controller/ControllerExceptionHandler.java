package io.github.albertus82.storage.controller;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {

	private final ErrorAttributes errorAttributes;

	@ExceptionHandler(ConstraintViolationException.class)
	ResponseEntity<Map<String, Object>> handleValidationException(Exception ex, WebRequest webRequest, HttpServletRequest httpRequest) {
		return handleException(ex, webRequest, httpRequest, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ NoSuchFileException.class, FileNotFoundException.class })
	ResponseEntity<Map<String, Object>> handleFileNotFoundException(Exception ex, WebRequest webRequest, HttpServletRequest httpRequest) {
		return handleException(ex, webRequest, httpRequest, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(FileAlreadyExistsException.class)
	ResponseEntity<Map<String, Object>> handleFileAlreadyExistsException(Exception ex, WebRequest webRequest, HttpServletRequest httpRequest) {
		return handleException(ex, webRequest, httpRequest, HttpStatus.CONFLICT);
	}

	private ResponseEntity<Map<String, Object>> handleException(@NonNull final Exception ex, @NonNull final WebRequest webRequest, @NonNull final HttpServletRequest httpRequest, @NonNull final HttpStatus status) {
		webRequest.setAttribute(RequestDispatcher.ERROR_EXCEPTION, ex, RequestAttributes.SCOPE_REQUEST);
		webRequest.setAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE, ex.getClass(), RequestAttributes.SCOPE_REQUEST);
		webRequest.setAttribute(RequestDispatcher.ERROR_MESSAGE, ex.getLocalizedMessage(), RequestAttributes.SCOPE_REQUEST);
		webRequest.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, httpRequest.getRequestURI(), RequestAttributes.SCOPE_REQUEST);
		webRequest.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, status.value(), RequestAttributes.SCOPE_REQUEST);
		final var body = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
		return new ResponseEntity<>(body, status);
	}

}
