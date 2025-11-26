package dev.marcos.spring_security.dto.user;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName
) {
}
