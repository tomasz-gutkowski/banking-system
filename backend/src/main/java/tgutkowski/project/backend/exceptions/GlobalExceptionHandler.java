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

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final HttpStatus VALIDATION_STATUS = HttpStatus.BAD_REQUEST;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> addError(errors, fe.getField(), fe.getDefaultMessage()));
        return buildValidationResponse(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        ex.getConstraintViolations()
                .forEach(v -> addError(errors, v.getPropertyPath().toString(), v.getMessage()));
        return buildValidationResponse(errors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> handleHandlerMethodValidation(HandlerMethodValidationException ex) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        ex.getParameterValidationResults().forEach(result ->
                result.getResolvableErrors().forEach(error -> {
                    String key = (error instanceof FieldError fe)
                            ? fe.getField()
                            : result.getMethodParameter().getParameterName();
                    addError(errors, key, error.getDefaultMessage());
                }));
        return buildValidationResponse(errors);
    }

    private static void addError(Map<String, List<String>> errors, String key, String message) {
        errors.computeIfAbsent(key, k -> new ArrayList<>()).add(message);
    }

    private static ResponseEntity<Map<String, Object>> buildValidationResponse(Map<String, List<String>> errors) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", String.valueOf(VALIDATION_STATUS.value()));
        body.put("message", "Validation failure");
        body.put("errors", errors);
        return ResponseEntity.status(VALIDATION_STATUS).body(body);
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
            HttpStatus status, String message, HttpServletRequest request) {

        ExceptionResponse exceptionResponse = ExceptionResponse.from(status.value(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(exceptionResponse);
    }

}
