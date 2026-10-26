package com.fabricio.security.service;

import com.fabricio.security.dto.LoginRequestDTO;
import com.fabricio.security.dto.RegisterRequestDTO;
import com.fabricio.security.dto.LoginResponseDTO;
import com.fabricio.security.dto.TokenRefreshRequestDTO;
import com.fabricio.security.entity.RefreshToken;
import com.fabricio.security.entity.User;
import com.fabricio.security.exception.RefreshTokenNotFoundException;
import com.fabricio.security.exception.UserAlreadyExistsException;
import com.fabricio.security.repositories.UserRepository;
import com.fabricio.security.security.UserAuthenticated;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(JwtService jwtService, UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public String register(RegisterRequestDTO registerRequestDTO){
        if (userRepository.existsByUsername(registerRequestDTO.username())){
            throw new UserAlreadyExistsException("Usuário já existente!");
        }

        User entity = new User();
        entity.setUsername(registerRequestDTO.username());
        entity.setPassword(passwordEncoder.encode(registerRequestDTO.password()));
        entity.setUserRole(User.UserRole.USER);
        userRepository.save(entity);

        return "Usuário criado com sucesso!";
    }

    public LoginResponseDTO login(LoginRequestDTO data){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(data.username(), data.password())
        );

        String accessToken = jwtService.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(data.username());

        return new LoginResponseDTO(accessToken, refreshToken.getToken());
    }

    public LoginResponseDTO refreshToken(TokenRefreshRequestDTO request) {
        return refreshTokenService.findByToken(request.refreshToken())
                .map(refreshTokenService::verifyExpiration) // Verifica a Validade do Token
                .map(oldToken -> {
                    RefreshToken newToken = refreshTokenService.rotateRefreshToken(oldToken);

                    var user = newToken.getUser();
                    var userDetails = new UserAuthenticated(user); // Cria um objeto de usuário autenticado baseado no token antigo

                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    String newAccessToken = jwtService.generateToken(authentication);

                    return new LoginResponseDTO(newAccessToken, newToken.getToken());
                })
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token inválido ou não encontrado!"));
    }
}