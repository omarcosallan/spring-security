package dev.marcos.spring_security.service;

import dev.marcos.spring_security.dto.login.LoginDTO;
import dev.marcos.spring_security.dto.login.TokenDTO;
import dev.marcos.spring_security.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        String token = tokenService.generateToken((User) Objects.requireNonNull(auth.getPrincipal()));
        return new TokenDTO(token);
    }
}
