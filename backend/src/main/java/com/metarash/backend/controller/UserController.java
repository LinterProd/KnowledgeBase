package com.metarash.backend.controller;

import com.metarash.backend.model.dto.request.UserCreateDto;
import com.metarash.backend.model.dto.request.UserUpdateDto;
import com.metarash.backend.model.dto.response.UserResponseDto;
import com.metarash.backend.model.entity.User;
import com.metarash.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserCreateDto dto) {
        log.info("UserController: creating user with dto: {}", dto.toString());
        User user = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(user));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(mapToDto(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUserById(#id).username == principal.username")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateDto dto) {
        User updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(mapToDto(updated));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users.stream().map(this::mapToDto).collect(Collectors.toList()));
    }

    @PostMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> suspendUser(@PathVariable Long id) {
        userService.suspendUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }

    private UserResponseDto mapToDto(User user) {
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.getStatus());
    }
}

//TODO добавить вывод инфы с контроллеров