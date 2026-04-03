package com.finance.finance_backend.Service;

import com.finance.finance_backend.Dto.UserRequest;
import com.finance.finance_backend.Dto.UserResponse;
import com.finance.finance_backend.Exception.ResourceNotFoundException;
import com.finance.finance_backend.Model.User;
import com.finance.finance_backend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + req.getEmail());
        }
        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .active(true)
                .build();
        return toResponse(userRepository.save(user));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        return toResponse(findUser(id));
    }

    public UserResponse updateStatus(Long id, boolean active) {
        User user = findUser(id);
        user.setActive(active);
        return toResponse(userRepository.save(user));
    }

    public UserResponse updateRole(Long id, UserRequest req) {
        User user = findUser(id);
        user.setRole(req.getRole());
        return toResponse(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        userRepository.delete(findUser(id));
    }
    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }
}

