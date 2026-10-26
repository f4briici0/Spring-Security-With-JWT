package com.fabricio.security.controller;

import com.fabricio.security.dto.LoginRequestDTO;
import com.fabricio.security.dto.RegisterRequestDTO;
import com.fabricio.security.dto.LoginResponseDTO;
import com.fabricio.security.dto.TokenRefreshRequestDTO;
import com.fabricio.security.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequestDTO data){
        return ResponseEntity.status(HttpStatus.OK).body(
                authenticationService.register(data)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO data){
        return ResponseEntity.status(HttpStatus.OK).body(
                authenticationService.login(data)
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponseDTO> refreshToken(@RequestBody @Valid TokenRefreshRequestDTO data) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authenticationService.refreshToken(data));
    }

}