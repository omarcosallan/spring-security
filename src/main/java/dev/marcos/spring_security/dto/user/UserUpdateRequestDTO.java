package dev.marcos.spring_security.dto.user;

public record UserUpdateRequestDTO(
        String firstName,
        String lastName
) {
}
