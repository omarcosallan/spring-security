package dev.marcos.spring_security.exception.handler;

import dev.marcos.spring_security.dto.error.ProblemDetail;
import dev.marcos.spring_security.exception.ConflictException;
import dev.marcos.spring_security.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

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

    private String getRequestPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
