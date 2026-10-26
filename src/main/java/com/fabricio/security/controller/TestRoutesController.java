package com.fabricio.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRoutesController {
    @GetMapping("/public")
    public String publicRoute(){
        return "This is a PUBLIC route!";
    }

    @GetMapping("/private")
    public String privateRoute(){
        return "This is a PRIVATE route!";
    }

    @GetMapping("/admin")
    public String adminRoute(){
        return "This is a ADMIN route!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/test")
    public String adminTest(){
        return "This is a ADMIN ROUTE WITH PRE AUTHORIZE route!";
    }
}