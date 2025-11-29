package dev.marcos.spring_security.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginDTO(
        @NotEmpty(message = "Username must not be empty")
        @Size(min = 3, max = 60, message = "Username must be between 3 and 60 characters")
        String username,

        @NotEmpty(message = "Password must not be empty")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message =
                        "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character")
        String password) {
}
