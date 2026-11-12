package tgutkowski.project.backend.exceptions;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tgutkowski.project.backend.exceptions.dtos.ExceptionResponse;

import java.io.IOException;

@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         @Nullable AuthenticationException authException) throws IOException{
        response.setContentType("application/json");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ExceptionResponse er = new ExceptionResponse(401, "Failed authentication" ,request.getRequestURI());
        response.getWriter().write(er.toJsonString());
    }
}
