package tgutkowski.project.backend.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
        return ResponseEntity.badRequest().body(errors);
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
