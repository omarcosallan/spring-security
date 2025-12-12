package dev.marcos.spring_security.security;

import dev.marcos.spring_security.dto.auth.TokenDTO;
import dev.marcos.spring_security.entity.User;
import dev.marcos.spring_security.entity.enums.Role;
import dev.marcos.spring_security.repository.UserRepository;
import dev.marcos.spring_security.service.TokenService;
import dev.marcos.spring_security.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        assert oAuth2User != null;
        String username = oAuth2User.getAttribute("login");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            email = username + "@github.com";
        }

        if (name == null) {
            name = username;
        }

        String finalEmail = email;
        String finalName = name;
        User user = userService.findByEmailOrUsername(finalEmail, username)
                .orElseGet(() -> createNewUser(username, finalEmail, finalName));

        String token = tokenService.generateToken(user.getUsername());

        TokenDTO tokenDTO = new TokenDTO(token);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), tokenDTO);
    }

    private User createNewUser(String username, String email, String name) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(name);
        user.getRoles().add(Role.ROLE_USER);

        return userRepository.save(user);
    }
}
