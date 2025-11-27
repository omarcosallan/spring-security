package dev.marcos.spring_security.dto.user;

import jakarta.validation.constraints.Size;

public record UserUpdateRequestDTO(
        @Size(max = 60, message = "First name must have a maximum of 60 characters")
        String firstName,

        @Size(max = 60, message = "Last name must have a maximum of 60 characters")
        String lastName
) {
}
