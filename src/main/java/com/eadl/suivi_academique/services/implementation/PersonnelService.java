package com.eadl.suivi_academique.services.implementation;

import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.eadl.suivi_academique.dto.PersonnelDTO;
import com.eadl.suivi_academique.entities.Personnel;
import com.eadl.suivi_academique.mappers.PersonnelMapper;
import com.eadl.suivi_academique.repositories.PersonnelRepository;
import com.eadl.suivi_academique.services.exceptions.personnelexception.PersonnelNotFoundException;
import com.eadl.suivi_academique.services.interfaces.PersonnelInterface;
import com.eadl.suivi_academique.utils.CodeGenerator;
import com.eadl.suivi_academique.utils.RolePersonnel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonnelService implements PersonnelInterface {

    private final PersonnelRepository personnelRepository;
    private final PersonnelMapper personnelMapper;
    private final CodeGenerator codeGenerator;
    private final PasswordEncoder passwordEncoder;

    // CREATE - Créer un nouveau personnel
    public PersonnelDTO createPersonnel(PersonnelDTO personnelDTO) {
        log.info("Tentative de création d'un nouveau personnel - login: {}, rôle: {}", 
                personnelDTO.getLoginPersonnel(), personnelDTO.getRolePersonnel());
        
        try {
            // Validation des données
            if (personnelDTO.getNomPersonnel() == null || personnelDTO.getNomPersonnel().isEmpty()) {
                log.error("Création personnel échouée - Nom obligatoire manquant");
                throw new IllegalArgumentException("Le nom du personnel est obligatoire");
            }
            
            if (personnelDTO.getLoginPersonnel() == null || personnelDTO.getLoginPersonnel().isEmpty()) {
                log.error("Création personnel échouée - Login obligatoire manquant");
                throw new IllegalArgumentException("Le login est obligatoire");
            }
            
            if (personnelDTO.getPasswordPersonnel() == null || personnelDTO.getPasswordPersonnel().isEmpty()) {
                log.error("Création personnel échouée - Mot de passe obligatoire manquant pour: {}", 
                         personnelDTO.getLoginPersonnel());
                throw new IllegalArgumentException("Le mot de passe est obligatoire");
            }
            
            // Vérifier si le login existe déjà
            if (personnelRepository.findByLoginPersonnel(personnelDTO.getLoginPersonnel()).isPresent()) {
                log.warn("Création personnel échouée - Login déjà existant: {}", 
                        personnelDTO.getLoginPersonnel());
                throw new IllegalArgumentException("Ce login existe déjà");
            }
            
            log.debug("Génération du code pour le rôle: {}", personnelDTO.getRolePersonnel());
            
            // Generate code based on role
            String generatedCode = codeGenerator.generate(personnelDTO.getRolePersonnel());
            if (generatedCode == null) {
                log.error("Échec génération du code pour le rôle: {}", personnelDTO.getRolePersonnel());
                throw new RuntimeException("Impossible de générer un code pour le rôle: " + 
                                         personnelDTO.getRolePersonnel());
            }
            
            log.debug("Code généré: {}", generatedCode);
            personnelDTO.setCodePersonnel(generatedCode);
            
            // Convert DTO to entity and ensure code is set
            Personnel personnel = new Personnel();
            personnel.setCodePersonnel(generatedCode);
            personnel.setNomPersonnel(personnelDTO.getNomPersonnel());
            personnel.setLoginPersonnel(personnelDTO.getLoginPersonnel());
            personnel.setPasswordPersonnel(passwordEncoder.encode(personnelDTO.getPasswordPersonnel()));
            personnel.setSexe(personnelDTO.getSexe());
            
            if (personnelDTO.getRolePersonnel() != null) {
                try {
                    personnel.setRolePersonnel(RolePersonnel.valueOf(personnelDTO.getRolePersonnel()));
                } catch (IllegalArgumentException e) {
                    log.error("Rôle invalide fourni: {}", personnelDTO.getRolePersonnel());
                    throw new IllegalArgumentException("Rôle invalide: " + personnelDTO.getRolePersonnel());
                }
            }
            
            log.debug("Sauvegarde du personnel en base de données");
            
            Personnel savedPersonnel = personnelRepository.save(personnel);
            
            log.info("Personnel créé avec succès - code: {}, nom: {}, login: {}, rôle: {}", 
                    savedPersonnel.getCodePersonnel(), 
                    savedPersonnel.getNomPersonnel(),
                    savedPersonnel.getLoginPersonnel(),
                    savedPersonnel.getRolePersonnel());
            
            return personnelMapper.toDTO(savedPersonnel);
            
        } catch (IllegalArgumentException e) {
            log.warn("Validation échouée lors de la création du personnel: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création du personnel: {}", 
                     personnelDTO.getLoginPersonnel(), e);
            throw e;
        }
    }

    // READ - Récupérer tous les personnels
    public List<PersonnelDTO> getAllPersonnels() {
        log.info("Récupération de tous les personnels");
        
        try {
            List<Personnel> personnels = personnelRepository.findAll();
            
            log.info("Nombre de personnels récupérés: {}", personnels.size());
            
            if (personnels.isEmpty()) {
                log.debug("Aucun personnel trouvé dans le système");
            }
            
            log.debug("Conversion de {} personnels en DTO", personnels.size());
            
            return personnels.stream()
                    .map(personnelMapper::toDTO)
                    .toList();
                    
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de tous les personnels", e);
            throw e;
        }
    }

    // Récupérer un personnel par son rôle
    public PersonnelDTO getPersonnelByRole(String rolePersonnel) {
        log.info("Recherche du personnel avec le rôle: {}", rolePersonnel);
        
        try {
            // Validation du rôle
            if (rolePersonnel == null || rolePersonnel.isEmpty()) {
                log.error("Recherche échouée - Rôle manquant");
                throw new IllegalArgumentException("Le rôle est obligatoire");
            }
            
            Optional<Personnel> personnel = Optional.ofNullable(
                personnelRepository.findByRolePersonnel(rolePersonnel)
            );
            
            return personnel.map(p -> {
                log.info("Personnel trouvé avec le rôle {} - code: {}, nom: {}", 
                        rolePersonnel, p.getCodePersonnel(), p.getNomPersonnel());
                return personnelMapper.toDTO(p);
            }).orElseThrow(() -> {
                log.warn("Aucun personnel trouvé avec le rôle: {}", rolePersonnel);
                return new PersonnelNotFoundException("Personnel non trouvé avec le rôle: " + rolePersonnel);
            });
            
        } catch (PersonnelNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la recherche du personnel par rôle: {}", rolePersonnel, e);
            throw e;
        }
    }

    // READ - Récupérer un personnel par son code
    public PersonnelDTO getPersonnelByCode(String codePersonnel) {
        log.info("Recherche du personnel avec le code: {}", codePersonnel);
        
        try {
            // Validation du code
            if (codePersonnel == null || codePersonnel.isEmpty()) {
                log.error("Recherche échouée - Code personnel manquant");
                throw new IllegalArgumentException("Le code personnel est obligatoire");
            }
            
            Optional<Personnel> personnel = personnelRepository.findById(codePersonnel);
            
            return personnel.map(p -> {
                log.info("Personnel trouvé - code: {}, nom: {}, rôle: {}", 
                        p.getCodePersonnel(), p.getNomPersonnel(), p.getRolePersonnel());
                log.debug("Détails du personnel - login: {}, sexe: {}", 
                         p.getLoginPersonnel(), p.getSexe());
                return personnelMapper.toDTO(p);
            }).orElseThrow(() -> {
                log.warn("Personnel non trouvé avec le code: {}", codePersonnel);
                return new PersonnelNotFoundException("Personnel non trouvé avec le code: " + codePersonnel);
            });
            
        } catch (PersonnelNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la recherche du personnel avec le code: {}", codePersonnel, e);
            throw e;
        }
    }

    // UPDATE - Mettre à jour un personnel
    public PersonnelDTO updatePersonnel(String codePersonnel, PersonnelDTO personnelDTO) {
        log.info("Tentative de mise à jour du personnel - code: {}", codePersonnel);
        
        try {
            // Validation du code
            if (codePersonnel == null || codePersonnel.isEmpty()) {
                log.error("Mise à jour échouée - Code personnel manquant");
                throw new IllegalArgumentException("Le code personnel est obligatoire");
            }
            
            Personnel personnel = personnelRepository.findById(codePersonnel)
                    .orElseThrow(() -> {
                        log.warn("Mise à jour impossible - Personnel non trouvé avec le code: {}", codePersonnel);
                        return new PersonnelNotFoundException("Personnel non trouvé avec le code: " + codePersonnel);
                    });

            log.debug("Personnel trouvé - Anciennes valeurs: nom={}, login={}, rôle={}", 
                     personnel.getNomPersonnel(), personnel.getLoginPersonnel(), personnel.getRolePersonnel());

            // Mettre à jour les champs
            personnel.setNomPersonnel(personnelDTO.getNomPersonnel());
            personnel.setLoginPersonnel(personnelDTO.getLoginPersonnel());
            
            if (personnelDTO.getPasswordPersonnel() != null && !personnelDTO.getPasswordPersonnel().isEmpty()) {
                log.debug("Mise à jour du mot de passe pour: {}", codePersonnel);
                personnel.setPasswordPersonnel(passwordEncoder.encode(personnelDTO.getPasswordPersonnel()));
            }
            
            personnel.setSexe(personnelDTO.getSexe());
            
            try {
                personnel.setRolePersonnel(RolePersonnel.valueOf(personnelDTO.getRolePersonnel()));
            } catch (IllegalArgumentException e) {
                log.error("Rôle invalide fourni lors de la mise à jour: {}", personnelDTO.getRolePersonnel());
                throw new IllegalArgumentException("Rôle invalide: " + personnelDTO.getRolePersonnel());
            }

            log.debug("Nouvelles valeurs: nom={}, login={}, rôle={}", 
                     personnelDTO.getNomPersonnel(), personnelDTO.getLoginPersonnel(), 
                     personnelDTO.getRolePersonnel());

            Personnel updatedPersonnel = personnelRepository.save(personnel);
            
            log.info("Personnel mis à jour avec succès - code: {}, nom: {}, rôle: {}", 
                    updatedPersonnel.getCodePersonnel(), 
                    updatedPersonnel.getNomPersonnel(),
                    updatedPersonnel.getRolePersonnel());
            
            return personnelMapper.toDTO(updatedPersonnel);
            
        } catch (PersonnelNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du personnel: {}", codePersonnel, e);
            throw e;
        }
    }

    // DELETE - Supprimer un personnel
    public void deletePersonnel(String codePersonnel) {
        log.info("Tentative de suppression du personnel - code: {}", codePersonnel);
        
        try {
            // Validation du code
            if (codePersonnel == null || codePersonnel.isEmpty()) {
                log.error("Suppression échouée - Code personnel manquant");
                throw new IllegalArgumentException("Le code personnel est obligatoire");
            }
            
            Personnel personnel = personnelRepository.findById(codePersonnel)
                    .orElseThrow(() -> {
                        log.warn("Suppression impossible - Personnel non trouvé avec le code: {}", codePersonnel);
                        return new PersonnelNotFoundException("Personnel non trouvé avec le code: " + codePersonnel);
                    });
            
            String nomPersonnel = personnel.getNomPersonnel(); // Pour le log après suppression
            String rolePersonnel = personnel.getRolePersonnel().name();
            
            personnelRepository.delete(personnel);
            
            log.info("Personnel supprimé avec succès - code: {}, nom: {}, rôle: {}", 
                    codePersonnel, nomPersonnel, rolePersonnel);
            
        } catch (PersonnelNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du personnel: {}", codePersonnel, e);
            throw e;
        }
    }

    // DELETE - Supprimer tous les personnels
    public void deleteAllPersonnels() {
        log.warn("Tentative de suppression de TOUS les personnels du système");
        
        try {
            long count = personnelRepository.count();
            
            log.warn("Suppression de {} personnels de la base de données", count);
            
            personnelRepository.deleteAll();
            
            log.warn("TOUS les personnels ont été supprimés ({} suppressions)", count);
            
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de tous les personnels", e);
            throw e;
        }
    }

    // Vérifier si un personnel existe
    public boolean personnelExists(String codePersonnel) {
        log.debug("Vérification de l'existence du personnel: {}", codePersonnel);
        
        try {
            // Validation du code
            if (codePersonnel == null || codePersonnel.isEmpty()) {
                log.error("Vérification échouée - Code personnel manquant");
                throw new IllegalArgumentException("Le code personnel est obligatoire");
            }
            
            boolean exists = personnelRepository.existsById(codePersonnel);
            
            log.debug("Personnel {} {} ", codePersonnel, exists ? "existe" : "n'existe pas");
            
            return exists;
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de l'existence du personnel: {}", codePersonnel, e);
            throw e;
        }
    }

    // Compter le nombre total de personnels
    public long countPersonnels() {
        log.debug("Comptage du nombre total de personnels");
        
        try {
            long count = personnelRepository.count();
            
            log.info("Nombre total de personnels dans le système: {}", count);
            
            return count;
            
        } catch (Exception e) {
            log.error("Erreur lors du comptage des personnels", e);
            throw e;
        }
    }
}