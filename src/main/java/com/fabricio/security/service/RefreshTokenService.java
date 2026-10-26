package com.fabricio.security.service;

import com.fabricio.security.entity.RefreshToken;
import com.fabricio.security.exception.RefreshTokenExpiredException;
import com.fabricio.security.exception.UserNotFoundException;
import com.fabricio.security.repositories.RefreshTokenRepository;
import com.fabricio.security.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    private final long REFRESH_DURATION_SECONDS = 86400L;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com o username: " + username));

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElse(new RefreshToken());

        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(REFRESH_DURATION_SECONDS));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken usedToken) {
        usedToken.setExpiryDate(Instant.now().plusSeconds(REFRESH_DURATION_SECONDS));
        usedToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(usedToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException("Refresh token expirou. Por favor, faça login novamente.");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(UUID userId) {
        refreshTokenRepository.deleteByUser_Id(userId);
    }
}