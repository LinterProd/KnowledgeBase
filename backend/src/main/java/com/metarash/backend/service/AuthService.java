package com.metarash.backend.service;

import com.metarash.backend.model.dto.request.AuthRequest;
import com.metarash.backend.model.dto.response.AuthResponse;
import com.metarash.backend.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getId());
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {
        if (jwtService.validateToken(refreshToken, true)) {
            Long userId = jwtService.getUserId(refreshToken, true);
            User user = userService.getUserById(userId);
            String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
            String newRefreshToken = jwtService.generateRefreshToken(user.getId());
            return new AuthResponse(newAccessToken, newRefreshToken);
        }
        throw new RuntimeException("Invalid refresh token");
    }
}