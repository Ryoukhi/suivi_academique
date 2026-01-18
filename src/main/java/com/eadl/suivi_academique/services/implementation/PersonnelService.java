package com.eadl.suivi_academique.services.implementation;

import com.eadl.suivi_academique.dto.PersonnelDTO;
import com.eadl.suivi_academique.entities.Personnel;
import com.eadl.suivi_academique.exceptions.personnelexception.PersonnelNotFoundException;
import com.eadl.suivi_academique.mappers.PersonnelMapper;
import com.eadl.suivi_academique.repositories.PersonnelRepository;
import com.eadl.suivi_academique.services.interfaces.PersonnelInterface;
import com.eadl.suivi_academique.utils.CodeGenerator;
import com.eadl.suivi_academique.utils.RolePersonnel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PersonnelService implements PersonnelInterface {

    private final PersonnelRepository personnelRepository;
    private final PersonnelMapper personnelMapper;
    private final CodeGenerator codeGenerator;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PersonnelDTO createPersonnel(PersonnelDTO dto) {
        log.info("Création d'un nouveau personnel : {}", dto.getLoginPersonnel());

        validateNewPersonnel(dto);

        String generatedCode = codeGenerator.generate(dto.getRolePersonnel());
        Assert.notNull(generatedCode, "Échec de génération du code matricule");

        Personnel personnel = Personnel.builder()
                .codePersonnel(generatedCode)
                .nomPersonnel(dto.getNomPersonnel())
                .loginPersonnel(dto.getLoginPersonnel())
                .passwordPersonnel(passwordEncoder.encode(dto.getPasswordPersonnel()))
                .sexe(dto.getSexe())
                .rolePersonnel(parseRole(dto.getRolePersonnel()))
                .build();

        return personnelMapper.toDTO(personnelRepository.save(personnel));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonnelDTO> getAllPersonnels() {
        return personnelRepository.findAll().stream()
                .map(personnelMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PersonnelDTO getPersonnelByCode(String code) {
        Assert.hasText(code, "Le code personnel est obligatoire");
        return personnelRepository.findById(code)
                .map(personnelMapper::toDTO)
                .orElseThrow(() -> new PersonnelNotFoundException("Personnel introuvable : " + code));
    }

    @Override
    @Transactional(readOnly = true)
    public PersonnelDTO getPersonnelByRole(String role) {
        Assert.hasText(role, "Le rôle est obligatoire");
        Personnel p = personnelRepository.findByRolePersonnel(role);
        if (p == null) {
            throw new PersonnelNotFoundException("Aucun personnel trouvé pour le rôle : " + role);
        }
        return personnelMapper.toDTO(p);
    }

    @Override
    public PersonnelDTO updatePersonnel(String code, PersonnelDTO dto) {
        log.info("Mise à jour du personnel code : {}", code);
        Assert.hasText(code, "Le code personnel est obligatoire");

        return personnelRepository.findById(code)
                .map(existing -> {
                    updatePersonnelFields(existing, dto);
                    return personnelMapper.toDTO(personnelRepository.save(existing));
                })
                .orElseThrow(() -> new PersonnelNotFoundException("Mise à jour impossible, personnel introuvable"));
    }

    @Override
    public void deletePersonnel(String code) {
        log.warn("Suppression du personnel : {}", code);
        Assert.hasText(code, "Le code personnel est obligatoire");
        
        if (!personnelRepository.existsById(code)) {
            throw new PersonnelNotFoundException("Suppression impossible : personnel introuvable");
        }
        personnelRepository.deleteById(code);
    }

    @Override
    public void deleteAllPersonnels() {
        log.error("ATTENTION : Suppression de TOUS les personnels");
        personnelRepository.deleteAll();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean personnelExists(String code) {
        return personnelRepository.existsById(code);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPersonnels() {
        return personnelRepository.count();
    }

    // --- Méthodes privées utilitaires ---

    private void validateNewPersonnel(PersonnelDTO dto) {
        Assert.hasText(dto.getNomPersonnel(), "Le nom est obligatoire");
        Assert.hasText(dto.getLoginPersonnel(), "Le login est obligatoire");
        Assert.hasText(dto.getPasswordPersonnel(), "Le mot de passe est obligatoire");

        if (personnelRepository.findByLoginPersonnel(dto.getLoginPersonnel()).isPresent()) {
            throw new IllegalArgumentException("Ce login existe déjà");
        }
    }

    private void updatePersonnelFields(Personnel entity, PersonnelDTO dto) {
        entity.setNomPersonnel(dto.getNomPersonnel());
        entity.setLoginPersonnel(dto.getLoginPersonnel());
        entity.setSexe(dto.getSexe());
        entity.setRolePersonnel(parseRole(dto.getRolePersonnel()));
        
        if (dto.getPasswordPersonnel() != null && !dto.getPasswordPersonnel().isBlank()) {
            entity.setPasswordPersonnel(passwordEncoder.encode(dto.getPasswordPersonnel()));
        }
    }

    private RolePersonnel parseRole(String roleStr) {
        try {
            return RolePersonnel.valueOf(roleStr.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Rôle invalide : " + roleStr);
        }
    }
}