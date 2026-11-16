package tgutkowski.project.backend.exceptions.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Schema(description = "Information about any non-validation error")
@JsonPropertyOrder({"timestamp", "status", "error", "path"})
public class ExceptionResponse{
    @Schema(description = "Date of when exception happened (UTC time)")
    private final Instant timestamp;
    @Schema(description = "HTTP error code of an exception (4xx/5xx)")
    private final int status;
    @Schema(description = "Error message")
    private final String error;
    @Schema(description = "URI path of a request that triggered exception")
    private final String path;

    public ExceptionResponse(int status, String error, String path) {
        this.timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public String toJsonString() {
        return "{\"timestamp\":\"" + timestamp.toString() + "\",\"status\":" + status + ",\"error\":\"" + error + "\",\"path\":\"" + path + "\"}";
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