package dev.marcos.spring_security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dev.marcos.spring_security.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {

    private final Algorithm algorithm;

    public TokenService(@Value("${spring.security.token.secret}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generateToken(User user) {
        return JWT.create().withSubject(user.getUsername()).withExpiresAt(Date.from(generateExpirationDate())).sign(algorithm);
    }

    public String validateToken(String token) {
        return JWT.require(algorithm).build().verify(token).getSubject();
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(24).toInstant(ZoneOffset.of("-03:00"));
    }
}
