package com.fabricio.security.controller;

import com.fabricio.security.dto.ChangeUserRoleDTO;
import com.fabricio.security.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/change-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeUserRole(@RequestBody @Valid ChangeUserRoleDTO data){
        return ResponseEntity.status(HttpStatus.OK).body(adminService.changeUserRole(data));
    }
}