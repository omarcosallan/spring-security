package dev.marcos.spring_security.dto.user;

public record UserRequestDTO(
        String username,
        String email,
        String firstName,
        String lastName,
        String password
) {
}
