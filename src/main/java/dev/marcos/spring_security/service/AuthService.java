package dev.marcos.spring_security.service;

import dev.marcos.spring_security.dto.auth.LoginDTO;
import dev.marcos.spring_security.dto.auth.RefreshTokenDTO;
import dev.marcos.spring_security.dto.auth.TokenDTO;
import dev.marcos.spring_security.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public TokenDTO authenticate(LoginDTO dto) {
        UsernamePasswordAuthenticationToken usernamePassword  = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
        Authentication auth = authenticationManager.authenticate(usernamePassword);
        User user = (User) Objects.requireNonNull(auth.getPrincipal());
        String token = tokenService.generateToken(user.getUsername());
        return new TokenDTO(token);
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

    public TokenDTO refresh(@Valid RefreshTokenDTO dto) {
        String username = tokenService.validateToken(dto.refreshToken());
        String token = tokenService.generateRefreshToken(username);
        return new TokenDTO(token);
    }
}
