package com.eadl.suivi_academique.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eadl.suivi_academique.dto.AuthRequest;
import com.eadl.suivi_academique.dto.AuthResponse;
import com.eadl.suivi_academique.dto.PersonnelDTO;
import com.eadl.suivi_academique.services.implementation.AuthentificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthentificationService authenticationService;

    @PostMapping("/login")
    public AuthResponse authenticate(@RequestBody AuthRequest request) {

        return authenticationService.authenticate(request);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody PersonnelDTO personnel
    ) {
        return ResponseEntity.ok(authenticationService.register(personnel));
    }
}
