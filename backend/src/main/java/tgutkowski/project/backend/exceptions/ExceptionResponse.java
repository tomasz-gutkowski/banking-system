package tgutkowski.project.backend.exceptions;

import java.time.LocalDateTime;

public record ExceptionResponse(
        int status,
        String message,
        String path,
        LocalDateTime timestamp
) {
    public static ExceptionResponse from(int status, String message, String path) {
        return new ExceptionResponse(status, message, path, LocalDateTime.now());
    }

    public String toJsonString() {
        return "{\"status\":\"" + status + "\",\"message\":\"" + message + "\",\"path\":\"" + path + "\",\"timestamp\":\"" + timestamp.toString() + "\"}";
    }
}