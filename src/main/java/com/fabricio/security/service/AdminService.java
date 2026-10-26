package com.fabricio.security.service;

import com.fabricio.security.dto.ChangeUserRoleDTO;
import com.fabricio.security.entity.User;
import com.fabricio.security.exception.UserNotFoundException;
import com.fabricio.security.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public String changeUserRole(ChangeUserRoleDTO data) {
        User entity = userRepository.findByUsername(data.username()).orElseThrow(
                () -> new UserNotFoundException("Usuário não encontrado com o username: " + data.username())
        );
        entity.setUserRole(data.newRole());
        userRepository.save(entity);
        return "Cargo alterado com sucesso!";
    }
}