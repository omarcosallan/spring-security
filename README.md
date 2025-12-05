# 🛡️ Guia de Implementação do Spring Security
Este repositório contém um exemplo de aplicação Spring Boot que demonstra a implementação e configuração básica do Spring Security.
O objetivo é fornecer autenticação básica e autorização baseada em URLs, protegendo endpoints específicos e permitindo acesso a outros.

## 1. Configuração do Projeto
Crie um novo projeto com [Spring Initializer](https://start.spring.io/) ou configure um projeto existente.

### 1.1 Dependências Maven
```
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security-test</artifactId>
    <scope>test</scope>
  </dependency>

  <dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>4.4.0</version>
  </dependency>
</dependencies>
```
> [!NOTE]
> 💡 Ao adicionar `spring-boot-starter-security`, o Spring Boot automaticamente exige autenticação para todos os endpoints por padrão.

### 1.2 Propriedade Secreta
Adicione a chave secreta para assinar os tokens JWT no seu application.properties (ou .yml):
```
Properties

spring.security.token.secret=SUA_CHAVE_SECRETA_MUITO_LONGA_E_COMPLEXA
```

## 2. Configuração do Spring Security
Para personalizar o comportamento padrão do Spring Security, é necessário criar uma classe de configuração. No Spring Boot 3+, o método recomendado é a definição de beans para a cadeia de filtros de segurança `SecurityFilterChain`.

### 2.1 Serviço de Token
```
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
```

### 2.2 Serviço de Usuário
```
package dev.marcos.spring_security.service;

import dev.marcos.spring_security.dto.user.UserRequestDTO;
import dev.marcos.spring_security.dto.user.UserResponseDTO;
import dev.marcos.spring_security.dto.user.UserUpdateRequestDTO;
import dev.marcos.spring_security.entity.User;
import dev.marcos.spring_security.entity.enums.Role;
import dev.marcos.spring_security.exception.ConflictException;
import dev.marcos.spring_security.exception.NotFoundException;
import dev.marcos.spring_security.mapper.UserMapper;
import dev.marcos.spring_security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
    }
}
```

### 2.3 Filtro de Segurança
```
package dev.marcos.spring_security.security;

import dev.marcos.spring_security.service.TokenService;
import dev.marcos.spring_security.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserService userService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            Optional<String> token = extractToken(request);

            if (token.isPresent()) {
                String subject = tokenService.validateToken(token.get());

                UserDetails user = userService.loadUserByUsername(subject);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            return Optional.empty();
        }

        return Optional.of(authHeader.replace("Bearer ", ""));
    }
}
```

### 2.4 Configuração Principal
```
package dev.marcos.spring_security.config;

import dev.marcos.spring_security.security.CustomAccessDeniedHandler;
import dev.marcos.spring_security.security.CustomAuthenticationEntryPoint;
import dev.marcos.spring_security.security.SecurityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.
                csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/users").permitAll()

                        // ADMIN only operations
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("ROLE_ADMIN")

                        // ADMIN or USER operations
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception ->
                        exception
                                .accessDeniedHandler(customAccessDeniedHandler)
                                .authenticationEntryPoint(customAuthenticationEntryPoint))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## 3. 📂 Estrutura de diretórios
```
src/main/java/dev/marcos/spring_security/
├── config/
│   └── SecurityConfig.java         <-- Configuração central de segurança
├── controller/
├── entity/
│   └── User.java                  <-- Implementação do UserDetails
├── exception/
│   └── (Handlers de Exceção)
├── repository/
├── security/
│   └── SecurityFilter.java         <-- Filtro de validação de JWT
├── service/
│   ├── TokenService.java           <-- Geração e Validação de Token
│   └── UserService.java            <-- Implementação do UserDetailsService
└── (Outros packages)
```
