package com.eadl.suivi_academique.services.implementation;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eadl.suivi_academique.config.JwtUtil;
import com.eadl.suivi_academique.dto.AuthRequest;
import com.eadl.suivi_academique.dto.AuthResponse;
import com.eadl.suivi_academique.dto.PersonnelDTO;
import com.eadl.suivi_academique.entities.Personnel;
import com.eadl.suivi_academique.repositories.PersonnelRepository;
import com.eadl.suivi_academique.services.interfaces.AuthentificationInterface;
import com.eadl.suivi_academique.utils.CodeGenerator;
import com.eadl.suivi_academique.utils.RolePersonnel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthentificationService implements AuthentificationInterface {

    private final PersonnelRepository personnelRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtService;
    private final AuthenticationManager authenticationManager;
    private final CodeGenerator codeGenerator;


    public AuthResponse authenticate(AuthRequest request) {
        log.info("Tentative d'authentification - login: {}", request.getLogin());
        
        try {
            Personnel p = personnelRepository.findByLoginPersonnel(request.getLogin())
                    .orElseThrow(() -> {
                        log.warn("Échec authentification - Personnel non trouvé: {}", request.getLogin());
                        return new RuntimeException("Personnel non trouvé");
                    });
            
            log.debug("Personnel trouvé - nom: {}, rôle: {}", 
                     p.getNomPersonnel(), p.getRolePersonnel());
            
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(),
                            request.getPassword()
                    )
            );
            
            log.info("Authentification réussie - login: {}, rôle: {}", 
                    request.getLogin(), p.getRolePersonnel());
            
            String jwtToken = jwtService.generateToken(p);
            
            return AuthResponse.builder()
                    .token(jwtToken)
                    .codePersonnel(p.getCodePersonnel())
                    .nomPersonnel(p.getNomPersonnel())
                    .rolePersonnel(p.getRolePersonnel().name())
                    .build();
                    
        } catch (BadCredentialsException e) {
            log.error("Échec authentification - Credentials invalides pour: {}", request.getLogin());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'authentification de: {}", 
                     request.getLogin(), e);
            throw e;
        }
    }

    public AuthResponse register(PersonnelDTO personnelDTO) {
        log.info("Tentative d'enregistrement nouveau personnel - login: {}, rôle: {}", 
                personnelDTO.getLoginPersonnel(), personnelDTO.getRolePersonnel());
        
        try {
            // Validation du mot de passe
            if (personnelDTO.getPasswordPersonnel() == null || 
                personnelDTO.getPasswordPersonnel().isEmpty()) {
                log.error("Enregistrement échoué - Mot de passe vide pour: {}", 
                         personnelDTO.getLoginPersonnel());
                throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
            }
            
            // Vérification si le login existe déjà
            if (personnelRepository.findByLoginPersonnel(personnelDTO.getLoginPersonnel()).isPresent()) {
                log.warn("Enregistrement échoué - Login déjà existant: {}", 
                        personnelDTO.getLoginPersonnel());
                throw new IllegalArgumentException("Ce login existe déjà");
            }
            
            log.debug("Génération du code pour le rôle: {}", personnelDTO.getRolePersonnel());
            
            // Generate code based on role
            String generatedCode = codeGenerator.generate(personnelDTO.getRolePersonnel());
            if (generatedCode == null) {
                log.error("Échec génération du code pour le rôle: {}", 
                         personnelDTO.getRolePersonnel());
                throw new RuntimeException("Impossible de générer un code pour le rôle: " 
                                         + personnelDTO.getRolePersonnel());
            }
            
            log.debug("Code généré: {}", generatedCode);
            personnelDTO.setCodePersonnel(generatedCode);
            
            // Convert DTO to entity
            Personnel personnel = new Personnel();
            personnel.setCodePersonnel(generatedCode);
            personnel.setNomPersonnel(personnelDTO.getNomPersonnel());
            personnel.setLoginPersonnel(personnelDTO.getLoginPersonnel());
            personnel.setSexe(personnelDTO.getSexe());
            
            // Secure role conversion
            try {
                personnel.setRolePersonnel(
                    RolePersonnel.valueOf(personnelDTO.getRolePersonnel().toUpperCase())
                );
            } catch (Exception e) {
                log.error("Rôle invalide fourni: {}", personnelDTO.getRolePersonnel());
                throw new IllegalArgumentException("Rôle invalide : " 
                                                 + personnelDTO.getRolePersonnel());
            }
            
            // Encode password (ne jamais logger le mot de passe!)
            personnel.setPasswordPersonnel(
                passwordEncoder.encode(personnelDTO.getPasswordPersonnel())
            );
            
            // Save
            Personnel savedPersonnel = personnelRepository.save(personnel);
            log.info("Personnel enregistré avec succès - code: {}, login: {}, rôle: {}", 
                    savedPersonnel.getCodePersonnel(), 
                    savedPersonnel.getLoginPersonnel(),
                    savedPersonnel.getRolePersonnel());
            
            // Generate JWT
            String jwtToken = jwtService.generateToken(savedPersonnel);
            
            return AuthResponse.builder()
                    .token(jwtToken)
                    .codePersonnel(savedPersonnel.getCodePersonnel())
                    .nomPersonnel(savedPersonnel.getNomPersonnel())
                    .rolePersonnel(savedPersonnel.getRolePersonnel().name())
                    .build();
                    
        } catch (Exception e) {
            log.error("Erreur lors de l'enregistrement du personnel: {}", 
                     personnelDTO.getLoginPersonnel(), e);
            throw e;
        }
    }

}

