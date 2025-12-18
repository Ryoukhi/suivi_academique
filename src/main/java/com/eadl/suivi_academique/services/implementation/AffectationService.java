package com.eadl.suivi_academique.services.implementation;

import java.util.List;
import org.springframework.stereotype.Service;
import com.eadl.suivi_academique.dto.AffectationDTO;
import com.eadl.suivi_academique.dto.CoursDTO;
import com.eadl.suivi_academique.dto.PersonnelDTO;
import com.eadl.suivi_academique.entities.Affectation;
import com.eadl.suivi_academique.entities.AffectationId;
import com.eadl.suivi_academique.entities.Cours;
import com.eadl.suivi_academique.entities.Personnel;
import com.eadl.suivi_academique.mappers.AffectationMapper;
import com.eadl.suivi_academique.mappers.CoursMapper;
import com.eadl.suivi_academique.mappers.PersonnelMapper;
import com.eadl.suivi_academique.repositories.AffectationRepository;
import com.eadl.suivi_academique.repositories.CoursRepository;
import com.eadl.suivi_academique.repositories.PersonnelRepository;
import com.eadl.suivi_academique.services.exceptions.affectationexception.AffectationNotFoundException;
import com.eadl.suivi_academique.services.exceptions.coursexception.CoursNotFoundException;
import com.eadl.suivi_academique.services.exceptions.personnelexception.PersonnelNotFoundException;
import com.eadl.suivi_academique.services.interfaces.AffectationInterface;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AffectationService implements AffectationInterface {
    
    private final AffectationRepository affectationRepository;
    private final PersonnelRepository personnelRepository;
    private final CoursRepository coursRepository;
    private final AffectationMapper affectationMapper;
    private final PersonnelMapper personnelMapper;
    private final CoursMapper coursMapper;
    
    public AffectationDTO create(AffectationDTO dto) {
        log.info("Tentative de création d'affectation - personnel: {}, cours: {}", 
                dto.getCodePersonnel(), dto.getCodeCours());
        
        try {
            // Validation des paramètres d'entrée
            if (dto.getCodePersonnel() == null || dto.getCodePersonnel().isEmpty()) {
                log.error("Création affectation échouée - Code personnel manquant");
                throw new IllegalArgumentException("Le code personnel est obligatoire");
            }
            
            if (dto.getCodeCours() == null || dto.getCodeCours().isEmpty()) {
                log.error("Création affectation échouée - Code cours manquant");
                throw new IllegalArgumentException("Le code cours est obligatoire");
            }
            
            // Vérifier si l'affectation existe déjà
            AffectationId existingId = new AffectationId(dto.getCodeCours(), dto.getCodePersonnel());
            if (affectationRepository.existsById(existingId)) {
                log.warn("Création affectation échouée - Affectation déjà existante pour personnel: {}, cours: {}", 
                        dto.getCodePersonnel(), dto.getCodeCours());
                throw new IllegalArgumentException("Cette affectation existe déjà");
            }
            
            log.debug("Recherche du personnel avec le code: {}", dto.getCodePersonnel());
            
            // Récupérer l'entité associée à codePersonnel
            Personnel personnel = personnelRepository.findById(dto.getCodePersonnel())
                    .orElseThrow(() -> {
                        log.error("Personnel non trouvé avec le code: {}", dto.getCodePersonnel());
                        return new PersonnelNotFoundException(
                            "Personnel non trouvé avec le code: " + dto.getCodePersonnel()
                        );
                    });
            
            log.debug("Personnel trouvé - nom: {}, rôle: {}", 
                     personnel.getNomPersonnel(), personnel.getRolePersonnel());
            
            log.debug("Recherche du cours avec le code: {}", dto.getCodeCours());
            
            // Récupérer l'entité associée à codeCours
            Cours cours = coursRepository.findById(dto.getCodeCours())
                    .orElseThrow(() -> {
                        log.error("Cours non trouvé avec le code: {}", dto.getCodeCours());
                        return new CoursNotFoundException(
                            "Cours non trouvé avec le code: " + dto.getCodeCours()
                        );
                    });
            
            log.debug("Cours trouvé - label: {}, crédits: {}", 
                     cours.getLabelCours(), cours.getNbCreditCours());
            
            // Conversion en DTO pour la réponse
            PersonnelDTO personnelDTO = personnelMapper.toDTO(personnel);
            CoursDTO coursDTO = coursMapper.toDTO(cours);
            
            // Création de l'affectation
            Affectation aff = new Affectation();
            
            // Clé composite
            AffectationId id = new AffectationId(dto.getCodeCours(), dto.getCodePersonnel());
            aff.setCodeAffectation(id);
            aff.setPersonnel(personnel);
            aff.setCours(cours);
            
            log.debug("Sauvegarde de l'affectation en base de données");
            
            Affectation saved = affectationRepository.save(aff);
            
            log.info("Affectation créée avec succès - personnel: {} ({}), cours: {} ({})", 
                    personnel.getCodePersonnel(), 
                    personnel.getNomPersonnel(),
                    cours.getCodeCours(),
                    cours.getLabelCours());
            
            AffectationDTO respDto = new AffectationDTO(
                    saved.getPersonnel().getCodePersonnel(), 
                    saved.getCours().getCodeCours(), 
                    personnelDTO, 
                    coursDTO
            );
            
            return respDto;
            
        } catch (PersonnelNotFoundException | CoursNotFoundException | IllegalArgumentException e) {
            log.warn("Échec de création d'affectation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création de l'affectation - personnel: {}, cours: {}", 
                     dto.getCodePersonnel(), dto.getCodeCours(), e);
            throw new RuntimeException("Erreur lors de la création de l'affectation", e);
        }
    }
    
    public List<AffectationDTO> getAll() {
        log.info("Récupération de toutes les affectations");
        
        try {
            List<Affectation> affectations = affectationRepository.findAll();
            
            log.info("Nombre d'affectations récupérées: {}", affectations.size());
            
            if (affectations.isEmpty()) {
                log.debug("Aucune affectation trouvée dans le système");
            }
            
            log.debug("Conversion de {} affectations en DTO", affectations.size());
            
            List<AffectationDTO> affectationDTOs = affectationMapper.toDtos(affectations);
            
            return affectationDTOs;
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de toutes les affectations", e);
            throw new RuntimeException("Erreur lors de la récupération des affectations", e);
        }
    }
    
    public AffectationDTO getById(String codeCours, String codePersonnel) {
        log.info("Recherche de l'affectation - personnel: {}, cours: {}", 
                codePersonnel, codeCours);
        
        try {
            // Validation des paramètres
            if (codePersonnel == null || codePersonnel.isEmpty()) {
                log.error("Recherche affectation échouée - Code personnel manquant");
                throw new IllegalArgumentException("Le code personnel est obligatoire");
            }
            
            if (codeCours == null || codeCours.isEmpty()) {
                log.error("Recherche affectation échouée - Code cours manquant");
                throw new IllegalArgumentException("Le code cours est obligatoire");
            }
            
            log.debug("Vérification de l'existence du personnel: {}", codePersonnel);
            
            Personnel personnel = personnelRepository.findById(codePersonnel)
                    .orElseThrow(() -> {
                        log.error("Personnel non trouvé avec le code: {}", codePersonnel);
                        return new PersonnelNotFoundException(
                            "Personnel non trouvé avec le code: " + codePersonnel
                        );
                    });
            
            log.debug("Personnel trouvé - nom: {}", personnel.getNomPersonnel());
            
            log.debug("Vérification de l'existence du cours: {}", codeCours);
            
            Cours cours = coursRepository.findById(codeCours)
                    .orElseThrow(() -> {
                        log.error("Cours non trouvé avec le code: {}", codeCours);
                        return new CoursNotFoundException(
                            "Cours non trouvé avec le code: " + codeCours
                        );
                    });
            
            log.debug("Cours trouvé - label: {}", cours.getLabelCours());
            
            PersonnelDTO personnelDTO = personnelMapper.toDTO(personnel);
            CoursDTO coursDTO = coursMapper.toDTO(cours);
            
            AffectationId id = new AffectationId(codeCours, codePersonnel);
            
            log.debug("Recherche de l'affectation avec l'ID composite");
            
            Affectation affectation = affectationRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Affectation non trouvée - personnel: {}, cours: {}", 
                                codePersonnel, codeCours);
                        return new AffectationNotFoundException(
                            "Affectation non trouvée pour le personnel " + codePersonnel + 
                            " et le cours " + codeCours
                        );
                    });
            
            log.info("Affectation trouvée - personnel: {} ({}), cours: {} ({})", 
                    personnel.getCodePersonnel(),
                    personnel.getNomPersonnel(),
                    cours.getCodeCours(),
                    cours.getLabelCours());
            
            return new AffectationDTO(
                    affectation.getPersonnel().getCodePersonnel(),
                    affectation.getCours().getCodeCours(),
                    personnelDTO,
                    coursDTO
            );
            
        } catch (PersonnelNotFoundException | CoursNotFoundException | 
                 AffectationNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de l'affectation - personnel: {}, cours: {}", 
                     codePersonnel, codeCours, e);
            throw new RuntimeException("Erreur lors de la recherche de l'affectation", e);
        }
    }
    
    public void delete(String codeCours, String codePersonnel) {
        log.info("Tentative de suppression de l'affectation - personnel: {}, cours: {}", 
                codePersonnel, codeCours);
        
        try {
            // Validation des paramètres
            if (codePersonnel == null || codePersonnel.isEmpty()) {
                log.error("Suppression affectation échouée - Code personnel manquant");
                throw new IllegalArgumentException("Le code personnel est obligatoire");
            }
            
            if (codeCours == null || codeCours.isEmpty()) {
                log.error("Suppression affectation échouée - Code cours manquant");
                throw new IllegalArgumentException("Le code cours est obligatoire");
            }
            
            AffectationId id = new AffectationId(codeCours, codePersonnel);
            
            log.debug("Vérification de l'existence de l'affectation");
            
            if (!affectationRepository.existsById(id)) {
                log.warn("Suppression impossible - Affectation non trouvée - personnel: {}, cours: {}", 
                        codePersonnel, codeCours);
                throw new AffectationNotFoundException(
                    "Affectation non trouvée pour le personnel " + codePersonnel + 
                    " et le cours " + codeCours
                );
            }
            
            // Récupérer les détails avant suppression pour le log
            Affectation affectation = affectationRepository.findById(id).orElseThrow();
            String nomPersonnel = affectation.getPersonnel().getNomPersonnel();
            String labelCours = affectation.getCours().getLabelCours();
            
            log.debug("Suppression de l'affectation en base de données");
            
            affectationRepository.deleteById(id);
            
            log.info("Affectation supprimée avec succès - personnel: {} ({}), cours: {} ({})", 
                    codePersonnel, nomPersonnel, codeCours, labelCours);
            
        } catch (AffectationNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de l'affectation - personnel: {}, cours: {}", 
                     codePersonnel, codeCours, e);
            throw new RuntimeException("Erreur lors de la suppression de l'affectation", e);
        }
    }
    
    
}