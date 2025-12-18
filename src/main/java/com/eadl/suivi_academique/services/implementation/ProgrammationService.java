package com.eadl.suivi_academique.services.implementation;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.eadl.suivi_academique.dto.ProgrammationDTO;
import com.eadl.suivi_academique.dto.ProgrammationRequest;
import com.eadl.suivi_academique.entities.Cours;
import com.eadl.suivi_academique.entities.Personnel;
import com.eadl.suivi_academique.entities.Programmation;
import com.eadl.suivi_academique.entities.Salle;
import com.eadl.suivi_academique.mappers.ProgrammationMapper;
import com.eadl.suivi_academique.mappers.ProgrammationReqMapper;
import com.eadl.suivi_academique.repositories.CoursRepository;
import com.eadl.suivi_academique.repositories.PersonnelRepository;
import com.eadl.suivi_academique.repositories.ProgrammationRepository;
import com.eadl.suivi_academique.repositories.SalleRepository;
import com.eadl.suivi_academique.services.exceptions.coursexception.CoursNotFoundException;
import com.eadl.suivi_academique.services.exceptions.personnelexception.PersonnelNotFoundException;
import com.eadl.suivi_academique.services.exceptions.programmationexception.ProgrammationNotFoundException;
import com.eadl.suivi_academique.services.exceptions.salleexception.SalleIndisponibleException;
import com.eadl.suivi_academique.services.exceptions.salleexception.SalleNotFoundException;
import com.eadl.suivi_academique.services.interfaces.ProgrammationInterface;
import com.eadl.suivi_academique.utils.StatusProgrammation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgrammationService implements ProgrammationInterface {
    
    private final ProgrammationRepository programmationRepository;
    private final ProgrammationMapper programmationMapper;
    private final SalleRepository salleRepository;
    private final CoursRepository coursRepository;
    private final PersonnelRepository personnelRepository;
    private final ProgrammationReqMapper programmationReqMapper;
    
    // CREATE
    public ProgrammationDTO createProgrammation(ProgrammationRequest dto) {
        log.info("Tentative de création d'une programmation - cours: {}, salle: {}, début: {}", 
                dto.getCodeCours(), dto.getCodeSalle(), dto.getDebutProgammation());
        
        try {
            // Validation des données d'entrée
            if (dto.getCodeSalle() == null || dto.getCodeSalle().isEmpty()) {
                log.error("Création programmation échouée - Code salle manquant");
                throw new IllegalArgumentException("Le code de la salle est obligatoire");
            }
            
            if (dto.getCodeCours() == null || dto.getCodeCours().isEmpty()) {
                log.error("Création programmation échouée - Code cours manquant");
                throw new IllegalArgumentException("Le code du cours est obligatoire");
            }
            
            if (dto.getDebutProgammation() == null || dto.getFinProgammation() == null) {
                log.error("Création programmation échouée - Dates manquantes");
                throw new IllegalArgumentException("Les dates de début et fin sont obligatoires");
            }
            
            // Vérification de la cohérence des dates
            if (dto.getFinProgammation().before(dto.getDebutProgammation())) {
                log.error("Création programmation échouée - Date de fin avant date de début");
                throw new IllegalArgumentException("La date de fin doit être après la date de début");
            }
            
            log.debug("Recherche de la salle: {}", dto.getCodeSalle());
            
            Salle salle = salleRepository.findByCodeSalle(dto.getCodeSalle())
                    .orElseThrow(() -> {
                        log.error("Salle non trouvée avec le code: {}", dto.getCodeSalle());
                        return new SalleNotFoundException("Salle non trouvée avec le code: " + dto.getCodeSalle());
                    });
            
            log.debug("Salle trouvée - libellé: {}, statut: {}, capacité: {}", 
                     salle.getDescSalle(), salle.getStatusSalle(), salle.getContenance());
            
            // Vérification du statut de la salle
            if (salle.getStatusSalle() == null || salle.getStatusSalle().toString().equals("FERMEE")) {
                log.warn("Création programmation impossible - Salle {} est fermée", salle.getCodeSalle());
                throw new SalleIndisponibleException(
                    "La salle avec le code " + salle.getCodeSalle() + " est fermée."
                );
            }
            
            if (salle.getStatusSalle().toString().equals("OCCUPEE")) {
                log.warn("Création programmation impossible - Salle {} est occupée", salle.getCodeSalle());
                throw new SalleIndisponibleException(
                    "La salle avec le code " + salle.getCodeSalle() + " est occupée."
                );
            }
            
            log.debug("Validation de la disponibilité de la salle réussie");
            log.debug("Conversion du DTO en entité");
            
            Programmation programmation = programmationReqMapper.toEntity(dto);
            
            log.debug("Sauvegarde de la programmation en base de données");
            
            Programmation savedProgrammation = programmationRepository.save(programmation);
            
            log.info("Programmation créée avec succès - code: {}, cours: {}, salle: {}, du {} au {}", 
                    savedProgrammation.getCodeProgrammation(),
                    savedProgrammation.getCours().getCodeCours(),
                    savedProgrammation.getSalle().getCodeSalle(),
                    savedProgrammation.getDebutProgammation(),
                    savedProgrammation.getFinProgammation());
            
            return programmationMapper.toDTO(savedProgrammation);
            
        } catch (SalleNotFoundException | SalleIndisponibleException | IllegalArgumentException e) {
            log.warn("Échec de création de la programmation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création de la programmation - cours: {}, salle: {}", 
                     dto.getCodeCours(), dto.getCodeSalle(), e);
            throw new RuntimeException("Erreur lors de la création de la programmation", e);
        }
    }
    
    // READ - all
    public List<ProgrammationDTO> getAllProgrammations() {
        log.info("Récupération de toutes les programmations");
        
        try {
            List<Programmation> programmations = programmationRepository.findAll();
            
            log.info("Nombre de programmations récupérées: {}", programmations.size());
            
            if (programmations.isEmpty()) {
                log.debug("Aucune programmation trouvée dans le système");
            }
            
            log.debug("Conversion de {} programmations en DTO", programmations.size());
            
            return programmationMapper.tDtos(programmations);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de toutes les programmations", e);
            throw new RuntimeException("Erreur lors de la récupération des programmations", e);
        }
    }
    
    // READ - by id
    public ProgrammationDTO getProgrammationById(int codeProgrammation) {
        log.info("Recherche de la programmation avec le code: {}", codeProgrammation);
        
        try {
            // Validation du code
            if (codeProgrammation <= 0) {
                log.error("Recherche échouée - Code programmation invalide: {}", codeProgrammation);
                throw new IllegalArgumentException("Le code de programmation doit être positif");
            }
            
            Optional<Programmation> programmation = programmationRepository.findById(codeProgrammation);
            
            return programmation.map(p -> {
                log.info("Programmation trouvée - code: {}, cours: {}, salle: {}", 
                        p.getCodeProgrammation(), 
                        p.getCours().getLabelCours(),
                        p.getSalle().getDescSalle());
                log.debug("Détails - heures: {}, début: {}, fin: {}, statut: {}", 
                         p.getNbHeureProgammation(),
                         p.getDebutProgammation(),
                         p.getFinProgammation(),
                         p.getStatusProgrammation());
                return programmationMapper.toDTO(p);
            }).orElseThrow(() -> {
                log.warn("Programmation non trouvée avec le code: {}", codeProgrammation);
                return new ProgrammationNotFoundException(
                    "Programmation non trouvée avec le code: " + codeProgrammation
                );
            });
            
        } catch (ProgrammationNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de la programmation: {}", codeProgrammation, e);
            throw new RuntimeException("Erreur lors de la recherche de la programmation", e);
        }
    }
    
    // UPDATE
    public ProgrammationDTO updateProgrammation(int codeProgrammation, ProgrammationRequest dto) {
        log.info("Tentative de mise à jour de la programmation - code: {}", codeProgrammation);
        
        try {
            // Validation du code
            if (codeProgrammation <= 0) {
                log.error("Mise à jour échouée - Code programmation invalide: {}", codeProgrammation);
                throw new IllegalArgumentException("Le code de programmation doit être positif");
            }
            
            log.debug("Recherche de la programmation à mettre à jour");
            
            Programmation programmation = programmationRepository.findById(codeProgrammation)
                    .orElseThrow(() -> {
                        log.warn("Mise à jour impossible - Programmation non trouvée avec le code: {}", 
                                codeProgrammation);
                        return new ProgrammationNotFoundException(
                            "Programmation non trouvée avec le code: " + codeProgrammation
                        );
                    });
            
            log.debug("Programmation trouvée - Anciennes valeurs: cours={}, salle={}, heures={}, statut={}", 
                     programmation.getCours().getCodeCours(),
                     programmation.getSalle().getCodeSalle(),
                     programmation.getNbHeureProgammation(),
                     programmation.getStatusProgrammation());
            
            // Vérification de la cohérence des dates
            if (dto.getFinProgammation() != null && dto.getDebutProgammation() != null 
                && dto.getFinProgammation().before(dto.getDebutProgammation())) {
                log.error("Mise à jour échouée - Date de fin avant date de début");
                throw new IllegalArgumentException("La date de fin doit être après la date de début");
            }
            
            log.debug("Recherche du cours: {}", dto.getCodeCours());
            
            Cours cours = coursRepository.findByCodeCours(dto.getCodeCours())
                    .orElseThrow(() -> {
                        log.error("Cours non trouvé avec le code: {}", dto.getCodeCours());
                        return new CoursNotFoundException("Cours non trouvé avec le code: " + dto.getCodeCours());
                    });
            
            log.debug("Cours trouvé - label: {}", cours.getLabelCours());
            
            log.debug("Recherche du personnel programmateur: {}", dto.getCodePersonnelProg());
            
            Personnel personnelProg = personnelRepository.findByCodePersonnel(dto.getCodePersonnelProg())
                    .orElseThrow(() -> {
                        log.error("Personnel programmateur non trouvé avec le code: {}", 
                                dto.getCodePersonnelProg());
                        return new PersonnelNotFoundException(
                            "Personnel non trouvé avec le code: " + dto.getCodePersonnelProg()
                        );
                    });
            
            log.debug("Personnel programmateur trouvé - nom: {}", personnelProg.getNomPersonnel());
            
            log.debug("Recherche du personnel validateur: {}", dto.getCodePersonnelVal());
            
            Personnel personnelVal = personnelRepository.findByCodePersonnel(dto.getCodePersonnelVal())
                    .orElseThrow(() -> {
                        log.error("Personnel validateur non trouvé avec le code: {}", 
                                dto.getCodePersonnelVal());
                        return new PersonnelNotFoundException(
                            "Personnel non trouvé avec le code: " + dto.getCodePersonnelVal()
                        );
                    });
            
            log.debug("Personnel validateur trouvé - nom: {}", personnelVal.getNomPersonnel());
            
            // Mettre à jour les champs
            programmation.setNbHeureProgammation(dto.getNbHeureProgammation());
            programmation.setDebutProgammation(dto.getDebutProgammation());
            programmation.setFinProgammation(dto.getFinProgammation());
            
            try {
                programmation.setStatusProgrammation(StatusProgrammation.valueOf(dto.getStatusProgrammation()));
            } catch (IllegalArgumentException e) {
                log.error("Statut de programmation invalide: {}", dto.getStatusProgrammation());
                throw new IllegalArgumentException("Statut de programmation invalide: " + 
                                                  dto.getStatusProgrammation());
            }
            
            programmation.setCours(cours);
            programmation.setPersonnelProg(personnelProg);
            programmation.setPersonnelVal(personnelVal);
            
            log.debug("Nouvelles valeurs: cours={}, heures={}, début={}, fin={}, statut={}", 
                     dto.getCodeCours(), dto.getNbHeureProgammation(), 
                     dto.getDebutProgammation(), dto.getFinProgammation(), 
                     dto.getStatusProgrammation());
            
            Programmation updated = programmationRepository.save(programmation);
            
            log.info("Programmation mise à jour avec succès - code: {}, cours: {}, statut: {}", 
                    updated.getCodeProgrammation(),
                    updated.getCours().getLabelCours(),
                    updated.getStatusProgrammation());
            
            return programmationMapper.toDTO(updated);
            
        } catch (ProgrammationNotFoundException | CoursNotFoundException | 
                 PersonnelNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour de la programmation: {}", codeProgrammation, e);
            throw new RuntimeException("Erreur lors de la mise à jour de la programmation", e);
        }
    }
    
    // DELETE - by id
    public void deleteProgrammation(int codeProgrammation) {
        log.info("Tentative de suppression de la programmation - code: {}", codeProgrammation);
        
        try {
            // Validation du code
            if (codeProgrammation <= 0) {
                log.error("Suppression échouée - Code programmation invalide: {}", codeProgrammation);
                throw new IllegalArgumentException("Le code de programmation doit être positif");
            }
            
            Programmation programmation = programmationRepository.findById(codeProgrammation)
                    .orElseThrow(() -> {
                        log.warn("Suppression impossible - Programmation non trouvée avec le code: {}", 
                                codeProgrammation);
                        return new ProgrammationNotFoundException(
                            "Programmation non trouvée avec le code: " + codeProgrammation
                        );
                    });
            
            String courLabel = programmation.getCours().getLabelCours();
            String salleLibelle = programmation.getSalle().getDescSalle();
            
            programmationRepository.delete(programmation);
            
            log.info("Programmation supprimée avec succès - code: {}, cours: {}, salle: {}", 
                    codeProgrammation, courLabel, salleLibelle);
            
        } catch (ProgrammationNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de la programmation: {}", codeProgrammation, e);
            throw new RuntimeException("Erreur lors de la suppression de la programmation", e);
        }
    }
    
    // DELETE - all
    public void deleteAllProgrammations() {
        log.warn("Tentative de suppression de TOUTES les programmations du système");
        
        try {
            long count = programmationRepository.count();
            
            log.warn("Suppression de {} programmations de la base de données", count);
            
            programmationRepository.deleteAll();
            
            log.warn("TOUTES les programmations ont été supprimées ({} suppressions)", count);
            
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de toutes les programmations", e);
            throw new RuntimeException("Erreur lors de la suppression des programmations", e);
        }
    }
}
	