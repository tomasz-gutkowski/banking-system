package tgutkowski.project.backend.exceptions.dtos;

import java.util.List;
import java.util.Map;

public class ValidationExceptionResponse extends ExceptionResponse {
    private final Map<String, List<String>> errors;

    public ValidationExceptionResponse(int status, String error, String path, Map<String, List<String>> errors) {
        super(status, error, path);
        this.errors = errors;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}
