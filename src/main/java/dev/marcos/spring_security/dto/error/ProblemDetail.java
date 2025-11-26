package dev.marcos.spring_security.dto.error;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProblemDetail {

    private final LocalDateTime timestamp;
    private final String title;
    private final String detail;
    private final int status;
    private final String instance;

    public ProblemDetail(String title, String detail, int status, String instance) {
        this.timestamp = LocalDateTime.now();
        this.title = title;
        this.detail = detail;
        this.status = status;
        this.instance = instance;
    }
}
