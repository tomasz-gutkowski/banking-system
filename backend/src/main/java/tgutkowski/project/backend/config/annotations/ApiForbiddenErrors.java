package tgutkowski.project.backend.config.annotations;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import tgutkowski.project.backend.exceptions.dtos.ExceptionResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "403", description = "Currently authenticated user is not authorized",
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
public @interface ApiForbiddenErrors {
}
