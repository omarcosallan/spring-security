package dev.marcos.spring_security.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotEmpty(message = "Username must not be empty")
        @Size(min = 3, max = 60, message = "Username must be between 3 and 60 characters")
        String username,

        @NotEmpty(message = "Email must not be empty")
        @Email(message = "Email should be valid")
        @Size(max = 150, message = "Email must have a maximum of 150 characters")
        String email,

        @NotEmpty(message = "First name must not be empty")
        @Size(min = 3, max = 60, message = "First name must be between 3 and 60 characters")
        String firstName,

        @Size(max = 60, message = "Last name must have a maximum of 60 characters")
        String lastName,

        @NotEmpty(message = "Password must not be empty")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message =
                        "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character")
        String password
) {
}
