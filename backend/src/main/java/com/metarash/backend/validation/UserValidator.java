package com.metarash.backend.validation;

import com.metarash.backend.model.dto.request.UserCreateDto;
import com.metarash.backend.model.dto.request.UserUpdateDto;
import com.metarash.backend.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateCreate(UserCreateDto dto) {
        if (dto == null) {
            throw new ValidationException("DTO is required");
        }
        if (StringUtils.isBlank(dto.username())) {
            throw new ValidationException("Username is required");
        }
        if (StringUtils.isBlank(dto.email())) {
            throw new ValidationException("Email is required");
        }
        if (StringUtils.isBlank(dto.password())) {
            throw new ValidationException("Password is required");
        }
        if (userRepository.existsByUsername(dto.username())) {
            throw new ValidationException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new ValidationException("Email already exists");
        }
        // Add password strength if needed
    }

    public void validateUpdate(UserUpdateDto dto, Long id) {
        if (dto == null) {
            throw new ValidationException("DTO is required");
        }
        if (dto.email() != null) {
            userRepository.findByEmail(dto.email())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new ValidationException("Email already exists");
                        }
                    });
        }
        if (dto.password() != null && dto.password().length() < 8) {  // Example
            throw new ValidationException("Password too weak");
        }
    }
}