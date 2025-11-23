package com.metarash.backend.service;

import com.metarash.backend.exception.EntityNotFoundException;
import com.metarash.backend.mapper.UserMapper;
import com.metarash.backend.model.dto.request.UserCreateDto;
import com.metarash.backend.model.dto.request.UserUpdateDto;
import com.metarash.backend.model.entity.User;
import com.metarash.backend.repository.UserRepository;
import com.metarash.backend.validation.UserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    @Transactional
    public User createUser(UserCreateDto dto) {
        userValidator.validateCreate(dto);
        User user = userMapper.toEntity(dto);
        user.setPasswordHash(passwordEncoder.encode(dto.password()));
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }

    @Transactional
    public User updateUser(Long id, UserUpdateDto dto) {
        User user = getUserById(id);
        userValidator.validateUpdate(dto, id);
        userMapper.updateFromDto(dto, user);
        if (dto.password() != null) {
            user.setPasswordHash(passwordEncoder.encode(dto.password()));
        }
        return userRepository.save(user);
    }

    @Transactional
    public void suspendUser(Long id) {
        getUserById(id);  // Ensure exists
        userRepository.suspendUser(id);
    }

    @Transactional
    public void activateUser(Long id) {
        getUserById(id);  // Ensure exists
        userRepository.activateUser(id);
    }
}