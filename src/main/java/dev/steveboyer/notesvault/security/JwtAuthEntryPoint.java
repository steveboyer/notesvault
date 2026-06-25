package dev.steveboyer.notesvault.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper objectMapper;

  public JwtAuthEntryPoint(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void commence(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull AuthenticationException authException)
      throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            "Valid authentication credentials are required to access this resource.");
    problemDetail.setTitle("Unauthorized");
    problemDetail.setProperty("timestamp", Instant.now());
    problemDetail.setProperty("path", request.getRequestURI());

    objectMapper.writeValue(response.getOutputStream(), problemDetail);
  }
}
