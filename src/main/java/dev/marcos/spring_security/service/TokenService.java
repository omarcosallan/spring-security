package dev.marcos.spring_security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import dev.marcos.spring_security.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

@Service
public class TokenService {

    private final Algorithm algorithm;

    public TokenService(@Value("${spring.security.token.secret}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generateToken(User user) {
        return JWT.create().withSubject(user.getUsername()).withExpiresAt(Date.from(generateExpirationDate())).sign(algorithm);
    }

    public Optional<String> validateToken(String token) {
        try {
            return Optional.of(JWT.require(algorithm).build().verify(token).getSubject());
        }
        catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(24).toInstant(ZoneOffset.of("-03:00"));
    }
}
