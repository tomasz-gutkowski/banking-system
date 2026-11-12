package tgutkowski.project.backend.exceptions.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@JsonPropertyOrder({"timestamp", "status", "error", "path"})
public class ExceptionResponse{
    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String path;

    public ExceptionResponse(int status, String error, String path) {
        this.timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public String toJsonString() {
        return "{\"timestamp\":\"" + timestamp.toString() + "\",\"status\":\"" + status + "\",\"message\":\"" + error + "\",\"path\":\"" + path + "\"}";
    }

    public String getPath() {
        return path;
    }

    public String getError() {
        return error;
    }

    public int getStatus() {
        return status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}