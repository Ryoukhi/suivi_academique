package com.eadl.suivi_academique.services.implementation;

import com.eadl.suivi_academique.config.JwtUtil;
import com.eadl.suivi_academique.dto.*;
import com.eadl.suivi_academique.entities.Personnel;
import com.eadl.suivi_academique.repositories.PersonnelRepository;
import com.eadl.suivi_academique.services.interfaces.AuthentificationInterface;
import com.eadl.suivi_academique.utils.CodeGenerator;
import com.eadl.suivi_academique.utils.RolePersonnel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthentificationService implements AuthentificationInterface {

    private final PersonnelRepository personnelRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtService;
    private final AuthenticationManager authenticationManager;
    private final CodeGenerator codeGenerator;

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        log.info("Tentative de connexion : {}", request.getLogin());

        // 1. Authentification via Spring Security (lève une BadCredentialsException si échec)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );

        // 2. Récupération du personnel (déjà validé par l'étape précédente)
        Personnel personnel = personnelRepository.findByLoginPersonnel(request.getLogin())
                .orElseThrow(() -> new RuntimeException("Erreur critique : Personnel introuvable après authentification"));

        log.info("Connexion réussie pour : {}", request.getLogin());
        return buildAuthResponse(personnel);
    }

    @Override
    @Transactional
    public AuthResponse register(PersonnelDTO dto) {
        log.info("Inscription nouveau personnel : {} (Rôle: {})", dto.getLoginPersonnel(), dto.getRolePersonnel());

        validateRegistrationRequest(dto);

        // 1. Préparation de l'entité
        String generatedCode = codeGenerator.generate(dto.getRolePersonnel());
        Assert.notNull(generatedCode, "Échec de génération du code matricule");

        Personnel personnel = Personnel.builder()
                .codePersonnel(generatedCode)
                .nomPersonnel(dto.getNomPersonnel())
                .loginPersonnel(dto.getLoginPersonnel())
                .sexe(dto.getSexe())
                .rolePersonnel(parseRole(dto.getRolePersonnel()))
                .passwordPersonnel(passwordEncoder.encode(dto.getPasswordPersonnel()))
                .build();

        // 2. Sauvegarde
        Personnel saved = personnelRepository.save(personnel);
        log.info("Personnel enregistré avec succès (ID: {})", saved.getCodePersonnel());

        return buildAuthResponse(saved);
    }

    /**
     * Méthodes utilitaires privées pour la clarté
     */

    private void validateRegistrationRequest(PersonnelDTO dto) {
        Assert.hasText(dto.getLoginPersonnel(), "Le login est obligatoire");
        Assert.hasText(dto.getPasswordPersonnel(), "Le mot de passe est obligatoire");
        Assert.hasText(dto.getRolePersonnel(), "Le rôle est obligatoire");

        if (personnelRepository.findByLoginPersonnel(dto.getLoginPersonnel()).isPresent()) {
            throw new IllegalArgumentException("Ce login est déjà utilisé");
        }
    }

    private RolePersonnel parseRole(String roleStr) {
        try {
            return RolePersonnel.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rôle non reconnu : " + roleStr);
        }
    }

    private AuthResponse buildAuthResponse(Personnel p) {
        return AuthResponse.builder()
                .token(jwtService.generateToken(p))
                .codePersonnel(p.getCodePersonnel())
                .nomPersonnel(p.getNomPersonnel())
                .rolePersonnel(p.getRolePersonnel().name())
                .build();
    }
}