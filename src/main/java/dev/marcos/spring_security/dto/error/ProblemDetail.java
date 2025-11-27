package dev.marcos.spring_security.dto.error;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ProblemDetail {

    private final LocalDateTime timestamp;
    private final String title;
    private final String detail;
    private final int status;
    private final String instance;
    private final Map<String, Object> properties;

    public ProblemDetail(String title, String detail, int status, String instance) {
        this.timestamp = LocalDateTime.now();
        this.title = title;
        this.detail = detail;
        this.status = status;
        this.instance = instance;
        this.properties = new HashMap<>();
    }

    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return properties;
    }
}
