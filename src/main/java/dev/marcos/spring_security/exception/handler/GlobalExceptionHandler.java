package dev.marcos.spring_security.exception.handler;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import dev.marcos.spring_security.dto.error.ProblemDetail;
import dev.marcos.spring_security.exception.ConflictException;
import dev.marcos.spring_security.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "Authentication error",
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                getRequestPath(request));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentialsException(HttpServletRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "Authentication failed",
                "Invalid username or password",
                HttpStatus.UNAUTHORIZED.value(),
                getRequestPath(request));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAuthorizationDeniedException(HttpServletRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "Forbidden",
                "You don't have permission to access this resource",
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        Map<String, String> errors =
                e.getBindingResult().getFieldErrors().stream()
                        .collect(
                                Collectors.toMap(
                                        FieldError::getField,
                                        fieldError ->
                                                Optional.ofNullable(fieldError.getDefaultMessage())
                                                        .orElse("Invalid value")));

        ProblemDetail problem = new ProblemDetail(
                "Validation error",
                "One or more fields are invalid",
                HttpStatus.BAD_REQUEST.value(),
                getRequestPath(request));

        problem.setProperty("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler({
            JWTVerificationException.class,
            JWTCreationException.class
    })
    public ResponseEntity<ProblemDetail> handleTokenException(
            JWTVerificationException ex, HttpServletRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "Invalid token",
                "The required authentication information is invalid. The token is either invalid or has expired",
                HttpStatus.UNAUTHORIZED.value(),
                getRequestPath(request)
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFound(
            NotFoundException ex, HttpServletRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "Resource not found",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                getRequestPath(request)
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflictException(
            ConflictException ex, HttpServletRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "Conflict error",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                getRequestPath(request)
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(HttpServletRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "Internal server error",
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                getRequestPath(request));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    private String getRequestPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
