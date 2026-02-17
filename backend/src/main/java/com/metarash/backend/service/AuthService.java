package com.metarash.backend.service;

import com.metarash.backend.mapper.UserMapper;
import com.metarash.backend.model.dto.request.AuthRequest;
import com.metarash.backend.model.dto.request.UserCreateDto;
import com.metarash.backend.model.dto.response.AuthResponse;
import com.metarash.backend.model.entity.RefreshToken;
import com.metarash.backend.model.entity.User;
import com.metarash.backend.repository.RefreshTokenRepository;
import com.metarash.backend.service.props.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;

    private final ConcurrentMap<Long, Object> userLocks = new ConcurrentHashMap<>();

    public AuthResponse registerAndLogin(UserCreateDto dto) {
        User user = userService.createUser(dto);
        return generateAuthResponse(user);
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = userService.getUserByUsername(authentication.getName());
        return generateAuthResponse(user);
    }

    public AuthResponse refresh(String refreshToken) {
        log.info("Refresh Token: {}", refreshToken);
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        Long userId = token.getUser().getId();

        synchronized (userLocks.computeIfAbsent(userId, _ -> new Object())) {
            try {
                if (token.getExpiresAt().isBefore(Instant.now())) {
                    refreshTokenRepository.delete(token);
                    throw new RuntimeException("Refresh token expired");
                }
                User user = token.getUser();
                updateRefreshToken(token);
                return generateAuthResponse(user);
            } finally {
                userLocks.remove(userId);
            }
        }
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        saveOrUpdateRefreshToken(user.getId(), refreshToken);
        return new AuthResponse(accessToken, refreshToken, userMapper.toDto(user));
    }

    private void updateRefreshToken(RefreshToken token) {
        String newRefreshToken = jwtService.generateRefreshToken(token.getUser().getId());
        token.setToken(newRefreshToken);
        token.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        refreshTokenRepository.save(token);
    }

    private void saveOrUpdateRefreshToken(Long userId, String refreshToken) {
        RefreshToken existingToken = refreshTokenRepository.findByUserId(userId)
                .orElse(new RefreshToken());

        existingToken.setUser(userService.getUserById(userId));
        existingToken.setToken(refreshToken);
        existingToken.setExpiresAt(Instant.now().plus(jwtProperties.getRefreshExpiration()));

        refreshTokenRepository.save(existingToken);
    }
}