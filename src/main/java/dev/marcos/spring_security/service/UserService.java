package dev.marcos.spring_security.service;

import dev.marcos.spring_security.dto.user.UserRequestDTO;
import dev.marcos.spring_security.dto.user.UserResponseDTO;
import dev.marcos.spring_security.entity.User;
import dev.marcos.spring_security.entity.enums.Role;
import dev.marcos.spring_security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserResponseDTO save(UserRequestDTO dto) {
        if (userRepository.existsByEmailOrUsername(dto.email(), dto.username())) {
            throw new RuntimeException("Email or username already exists");
        }

        User user = new User();
        user.setEmail(dto.email());
        user.setUsername(dto.username());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.getRoles().add(Role.ROLE_USER);
        user.setPassword(new BCryptPasswordEncoder().encode(dto.password()));

        userRepository.save(user);

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(u -> new UserResponseDTO(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getFirstName(),
                        u.getLastName()
                ))
                .toList();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
}
