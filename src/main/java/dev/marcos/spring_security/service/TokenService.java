package dev.marcos.spring_security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class TokenService {

    private final Algorithm algorithm;

    public TokenService(@Value("${spring.security.token.secret}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generateToken(String username) {
        return JWT.create().withSubject(username).withExpiresAt(Date.from(generateExpirationHours(1))).sign(algorithm);
    }

    public String validateToken(String token) {
        return JWT.require(algorithm).build().verify(token).getSubject();
    }

    public String generateRefreshToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(Date.from(generateExpirationHours(24)))
                .sign(algorithm);
    }

    private Instant generateExpirationHours(int amount) {
        return Instant.now().plus(amount, ChronoUnit.HOURS);
    }
}
