package com.otushomework.authservice.controller;

import com.otushomework.authservice.dto.UserDTO;
import com.otushomework.authservice.model.RefreshToken;
import com.otushomework.authservice.repository.RefreshTokenRepository;
import com.otushomework.authservice.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public AuthController(AuthService authService, RefreshTokenRepository refreshTokenRepository) {
        this.authService = authService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password) {
        Optional<UserDTO> userDtoOptional = authService.getUserByUsername(username);
        if (userDtoOptional .isEmpty()) {
            return ResponseEntity.status(401).body("Логин или пароль неверен");
        }

        UserDTO userDto = userDtoOptional.get();

        if (!passwordEncoder().matches(password, userDto.getPassword())) {
            return ResponseEntity.status(401).body("Логин или пароль неверен");
        }

        String accessToken = createAccessToken(userDtoOptional.get());
        RefreshToken refreshToken = createRefreshToken(userDtoOptional.get());
        refreshTokenRepository.save(refreshToken);
        Map<String, Object> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken.getToken());
        return ResponseEntity.ok(tokens);
    }

    private String createAccessToken(UserDTO user) {
        Date now = new Date();
        Date expiryDate = Date.from(LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("user_name", user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    private String generateRefreshToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[64];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private RefreshToken createRefreshToken(UserDTO user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID().toString());
        refreshToken.setUserId(String.valueOf(user.getId()));
        refreshToken.setToken(generateRefreshToken());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        return refreshToken;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByToken(refreshToken);

        if (tokenOptional.isEmpty() || tokenOptional.get().isExpired()) {
            return ResponseEntity.status(401).body("Невалидный токен refresh token");
        }
        System.out.println(tokenOptional.get());
        Optional<UserDTO> optionalUser = authService.getUserById(tokenOptional.get().getUserId());
        if (optionalUser.isPresent()) {
            String newAccessToken = createAccessToken(optionalUser.get());

            RefreshToken existingToken = tokenOptional.get();
            existingToken.setExpiryDate(LocalDateTime.now().plusDays(7));
            refreshTokenRepository.save(existingToken);

            Map<String, Object> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);
            tokens.put("refreshToken", existingToken.getToken());
            return ResponseEntity.ok(tokens);
        } else {
            return ResponseEntity.status(401).body("Пользователь не найден или недействительный refresh токен");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String refreshToken) {
        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByToken(refreshToken);
        tokenOptional.ifPresent(refreshTokenRepository::delete);
        return ResponseEntity.ok("Пользователь вышел из системы");
    }

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
