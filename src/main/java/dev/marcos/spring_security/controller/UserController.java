package dev.marcos.spring_security.controller;

import dev.marcos.spring_security.dto.user.UserRequestDTO;
import dev.marcos.spring_security.dto.user.UserResponseDTO;
import dev.marcos.spring_security.dto.user.UserUpdateRequestDTO;
import dev.marcos.spring_security.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> save(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(dto));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or principal.id == #id")
    public ResponseEntity<UserResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequestDTO dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
