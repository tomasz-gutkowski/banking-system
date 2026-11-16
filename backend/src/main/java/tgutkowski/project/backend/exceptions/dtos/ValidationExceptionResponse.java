package tgutkowski.project.backend.exceptions.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "Information about a validation error")
public class ValidationExceptionResponse extends ExceptionResponse {
    @Schema(description = "List of fields that validation did not pass on together with list of descriptions for not fulfilled requirements"
    , example = "{" +
                "\"username\": [" +
                    "\"Username can only consist of letters, numbers, dots, underscores and hyphens\"," +
                    "\"Username must be 3-30 characters\"" +
                "]," +
                "\"password\": [" +
                    "\"Password must be 8-72 characters\"" +
                "]" +
            "}")
    private final Map<String, List<String>> errors;

    public ValidationExceptionResponse(int status, String error, String path, Map<String, List<String>> errors) {
        super(status, error, path);
        this.errors = errors;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}
