package tgutkowski.project.backend.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import tgutkowski.project.backend.exceptions.dtos.ExceptionResponse;
import tgutkowski.project.backend.exceptions.dtos.ValidationExceptionResponse;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final HttpStatus VALIDATION_STATUS = HttpStatus.BAD_REQUEST;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationExceptionResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> addError(errors, fe.getField(), fe.getDefaultMessage()));
        return buildValidationResponse(errors, request.getRequestURI());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationExceptionResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        ex.getConstraintViolations()
                .forEach(v -> addError(errors, v.getPropertyPath().toString(), v.getMessage()));
        return buildValidationResponse(errors, request.getRequestURI());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ValidationExceptionResponse> handleHandlerMethodValidation(HandlerMethodValidationException ex, HttpServletRequest request) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        ex.getParameterValidationResults().forEach(result ->
                result.getResolvableErrors().forEach(error -> {
                    String key = (error instanceof FieldError fe)
                            ? fe.getField()
                            : result.getMethodParameter().getParameterName();
                    addError(errors, key, error.getDefaultMessage());
                }));
        return buildValidationResponse(errors, request.getRequestURI());
    }

    private static void addError(Map<String, List<String>> errors, String key, String message) {
        errors.computeIfAbsent(key, k -> new ArrayList<>()).add(message);
    }

    private static ResponseEntity<ValidationExceptionResponse> buildValidationResponse(Map<String, List<String>> errors, String path) {
        ValidationExceptionResponse exceptionResponse = new ValidationExceptionResponse(
                VALIDATION_STATUS.value(),
                "Validation failure",
                path,
                errors);
        return ResponseEntity.status(VALIDATION_STATUS).body(exceptionResponse);
    }

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidArgument(InvalidArgumentException e, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionResponse> handleConflict(ConflictException e, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, e.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ExceptionResponse> handleInsufficientBalance(InsufficientBalanceException e, HttpServletRequest request) {
        return build(HttpStatus.PAYMENT_REQUIRED, e.getMessage(), request);
    }

    @ExceptionHandler(AccountNotEligibleException.class)
    public ResponseEntity<ExceptionResponse> handleAccountNotEligible(AccountNotEligibleException e, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, e.getMessage(), request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidCredentials(InvalidCredentialsException e, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, e.getMessage(), request);
    }

    @ExceptionHandler(NotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleNotPermitted(NotPermittedException e, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, e.getMessage(), request);
    }

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<ExceptionResponse> handleUnexpected(InternalException e, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), request);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidRefreshToken(InvalidTokenException e, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, e.getMessage(), request);
    }

    private ResponseEntity<ExceptionResponse> build(
            HttpStatus status, String error, HttpServletRequest request) {

        ExceptionResponse exceptionResponse = new ExceptionResponse(status.value(), error, request.getRequestURI());
        return ResponseEntity.status(status).body(exceptionResponse);
    }

}
