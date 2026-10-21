package tgutkowski.project.backend.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.rmi.UnexpectedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e){
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.unprocessableContent().body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleValidation(ConstraintViolationException e){
        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach(error -> {
            errors.put(error.getPropertyPath().toString(), error.getMessage());
        });
        return ResponseEntity.unprocessableContent().body(errors);
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
        return build(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage(), request);
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
    public ResponseEntity<ExceptionResponse> handleUnexpected(UnexpectedException e, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), request);
    }
//    //unexpected
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ExceptionResponse> handleGeneral(Exception ex, HttpServletRequest request) {
//        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", request);
//    }

    private ResponseEntity<ExceptionResponse> build(
            HttpStatus status, String message, HttpServletRequest request) {

        ExceptionResponse exceptionResponse = ExceptionResponse.from(status.value(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(exceptionResponse);
    }

}
