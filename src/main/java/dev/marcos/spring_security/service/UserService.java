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
    private final UserMapper userMapper;

    public UserResponseDTO save(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new ConflictException("Email already exists, please, try other email address");
        }
        if (userRepository.existsByUsername(dto.username())) {
            throw new ConflictException("Username already exists, please, try other username");
        }

        User user = userMapper.toEntity(dto);
        user.getRoles().add(Role.ROLE_USER);
        user.setPassword(new BCryptPasswordEncoder().encode(dto.password()));

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDTO).toList();
    }

    public UserResponseDTO update(UUID id, UserUpdateRequestDTO dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        if (dto.firstName() != null) {
            user.setFirstName(dto.firstName());
        }
        if (dto.lastName() != null) {
            user.setLastName(dto.lastName());
        }

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public void delete(UUID id) {
        User user = userRepository.findByIdAndActiveIsTrue(id).orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        User currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert currentUser != null;

        if (user.getId().equals(currentUser.getId())) {
            throw new ConflictException("You cannot delete your account");
        }

        if (user.getRoles().contains(Role.ROLE_ADMIN)) {
            throw new ConflictException("You cannot delete an admin user");
        }

        user.setActive(false);

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
    }
}
