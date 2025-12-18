package com.eadl.suivi_academique.services.implementation;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.eadl.suivi_academique.config.CurrentUserProvider;
import com.eadl.suivi_academique.config.RequestContextProvider;
import com.eadl.suivi_academique.dto.SalleDTO;
import com.eadl.suivi_academique.entities.Salle;
import com.eadl.suivi_academique.exceptions.salleexception.InvalidSalleException;
import com.eadl.suivi_academique.exceptions.salleexception.SalleNotFoundException;
import com.eadl.suivi_academique.mappers.SalleMapper;
import com.eadl.suivi_academique.repositories.SalleRepository;
import com.eadl.suivi_academique.services.interfaces.SalleInterface;
import com.eadl.suivi_academique.utils.SalleStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalleService implements SalleInterface {

    private final RequestContextProvider requestContextProvider;
    private final CurrentUserProvider currentUserProvider;
    private final SalleRepository salleRepository;
    private final SalleMapper salleMapper;
    
    // CREATE - Créer une nouvelle salle
    public SalleDTO createSalle(SalleDTO salleDTO) {

        String username = currentUserProvider.getUsername();
        String ip = requestContextProvider.getClientIp();
        String userAgent = requestContextProvider.getUserAgent();

        log.info("Tentative de création d'une nouvelle salle par l'utilisateur \"{}\" | ip: {} | device: {}  - code: {}, libellé: {}", 
                username, ip, userAgent, salleDTO.getCodeSalle(), salleDTO.getDescSalle());
        
        try {
            // Validation des données
            if (salleDTO.getCodeSalle() == null || salleDTO.getCodeSalle().isEmpty()) {
                log.error("Création salle échouée - Code salle manquant");
                throw new InvalidSalleException("Le code de la salle est obligatoire");
            }
            
            if (salleDTO.getStatusSalle() == null || salleDTO.getStatusSalle().isEmpty()) {
                log.error("Création salle échouée - Statut salle manquant pour: {}", 
                         salleDTO.getCodeSalle());
                throw new InvalidSalleException("Le statut de la salle est obligatoire");
            }
            
            if (salleDTO.getDescSalle() == null || salleDTO.getDescSalle().isEmpty()) {
                log.error("Création salle échouée - Libellé manquant pour: {}", 
                         salleDTO.getCodeSalle());
                throw new InvalidSalleException("Le libellé de la salle est obligatoire");
            }
            
            if (salleDTO.getContenance() != 0 && salleDTO.getContenance() <= 0) {
                log.error("Création salle échouée - Contenance invalide: {} pour salle: {}", 
                         salleDTO.getContenance(), salleDTO.getCodeSalle());
                throw new InvalidSalleException("La contenance doit être supérieure à zéro");
            }
            
            // Vérifier si la salle existe déjà
            if (salleRepository.existsById(salleDTO.getCodeSalle())) {
                log.warn("Création salle échouée - Salle déjà existante avec le code: {}", 
                        salleDTO.getCodeSalle());
                throw new InvalidSalleException("Une salle existe déjà avec le code: " + 
                                                  salleDTO.getCodeSalle());
            }
            
            // Validation du statut
            try {
                SalleStatus.valueOf(salleDTO.getStatusSalle());
            } catch (IllegalArgumentException e) {
                log.error("Statut de salle invalide: {} pour salle: {}", 
                         salleDTO.getStatusSalle(), salleDTO.getCodeSalle());
                throw new InvalidSalleException("Statut de salle invalide: " + 
                                                  salleDTO.getStatusSalle());
            }
            
            log.debug("Validation réussie - Conversion DTO vers entité pour: {}", 
                     salleDTO.getCodeSalle());
            
            Salle salle = salleMapper.toEntity(salleDTO);
            
            log.debug("Sauvegarde de la salle en base de données");
            
            Salle savedSalle = salleRepository.save(salle);
            
            log.info("Salle créée avec succès - code: {}, libellé: {}, contenance: {}, statut: {}", 
                    savedSalle.getCodeSalle(), 
                    savedSalle.getDescSalle(),
                    savedSalle.getContenance(),
                    savedSalle.getStatusSalle());
            
            return salleMapper.toDTO(savedSalle);
            
        } catch (InvalidSalleException | IllegalArgumentException e) {
            log.warn("Validation échouée lors de la création de la salle: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création de la salle: {}", 
                     salleDTO.getCodeSalle(), e);
            throw new RuntimeException("Erreur lors de la création de la salle", e);
        }
    }
    
    // READ - Récupérer toutes les salles
    public List<SalleDTO> getAllSalles() {
        log.info("Récupération de toutes les salles");
        
        try {
            List<Salle> salles = salleRepository.findAll();
            
            log.info("Nombre de salles récupérées: {}", salles.size());
            
            if (salles.isEmpty()) {
                log.debug("Aucune salle trouvée dans le système");
            }
            
            log.debug("Conversion de {} salles en DTO", salles.size());
            
            return salles.stream()
                    .map(salleMapper::toDTO)
                    .toList();
                    
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de toutes les salles", e);
            throw new RuntimeException("Erreur lors de la récupération des salles", e);
        }
    }
    
    // READ - Récupérer une salle par son code
    public SalleDTO getSalleByCode(String codeSalle) {
        log.info("Recherche de la salle avec le code: {}", codeSalle);
        
        try {
            // Validation du code
            if (codeSalle == null || codeSalle.isEmpty()) {
                log.error("Recherche échouée - Code salle manquant");
                throw new IllegalArgumentException("Le code de la salle est obligatoire");
            }
            
            Optional<Salle> salle = salleRepository.findById(codeSalle);
            
            return salle.map(s -> {
                log.info("Salle trouvée - code: {}, libellé: {}, statut: {}", 
                        s.getCodeSalle(), s.getDescSalle(), s.getStatusSalle());
                log.debug("Détails de la salle - contenance: {}, capacité: {}", 
                         s.getContenance(), s.getContenance());
                return salleMapper.toDTO(s);
            }).orElseThrow(() -> {
                log.warn("Salle non trouvée avec le code: {}", codeSalle);
                return new SalleNotFoundException("Salle non trouvée avec le code: " + codeSalle);
            });
            
        } catch (SalleNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de la salle avec le code: {}", codeSalle, e);
            throw new RuntimeException("Erreur lors de la recherche de la salle", e);
        }
    }
    
    // READ - Récupérer les salles avec une contenance minimum
    public List<SalleDTO> getSallesByMinContenance(int minContenance) {
        log.info("Recherche des salles avec une contenance minimale de: {}", minContenance);
        
        try {
            // Validation de la contenance
            if (minContenance < 0) {
                log.error("Recherche échouée - Contenance minimale invalide: {}", minContenance);
                throw new IllegalArgumentException("La contenance minimale ne peut pas être négative");
            }
            
            List<Salle> salles = salleRepository.findByContenanceGreaterThanEquals(minContenance);
            
            log.info("Nombre de salles trouvées avec contenance >= {}: {}", 
                    minContenance, salles.size());
            
            if (salles.isEmpty()) {
                log.debug("Aucune salle trouvée avec une contenance >= {}", minContenance);
            }
            
            return salles.stream()
                    .map(salleMapper::toDTO)
                    .toList();
                    
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de salles par contenance minimum: {}", 
                     minContenance, e);
            throw new RuntimeException("Erreur lors de la recherche des salles", e);
        }
    }
    
    // READ - Récupérer les salles par statut
    public List<SalleDTO> getSallesByStatus(SalleStatus status) {
        log.info("Recherche des salles avec le statut: {}", status);
        
        try {
            // Validation du statut
            if (status == null) {
                log.error("Recherche échouée - Statut salle manquant");
                throw new IllegalArgumentException("Le statut de la salle est obligatoire");
            }
            
            List<Salle> allSalles = salleRepository.findAll();
            
            log.debug("Filtrage de {} salles par statut: {}", allSalles.size(), status);
            
            List<SalleDTO> salleDTOs = allSalles.stream()
                    .filter(salle -> salle.getStatusSalle() == status)
                    .map(salleMapper::toDTO)
                    .toList();
            
            log.info("Nombre de salles trouvées avec le statut {}: {}", status, salleDTOs.size());
            
            if (salleDTOs.isEmpty()) {
                log.debug("Aucune salle trouvée avec le statut: {}", status);
            }
            
            return salleDTOs;
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de salles par statut: {}", status, e);
            throw new RuntimeException("Erreur lors de la recherche des salles", e);
        }
    }
    
    // UPDATE - Mettre à jour une salle
    public SalleDTO updateSalle(String codeSalle, SalleDTO salleDTO) {
        log.info("Tentative de mise à jour de la salle - code: {}", codeSalle);
        
        try {
            // Validation du code
            if (codeSalle == null || codeSalle.isEmpty()) {
                log.error("Mise à jour échouée - Code salle manquant");
                throw new IllegalArgumentException("Le code de la salle est obligatoire");
            }
            
            Salle salle = salleRepository.findById(codeSalle)
                    .orElseThrow(() -> {
                        log.warn("Mise à jour impossible - Salle non trouvée avec le code: {}", codeSalle);
                        return new SalleNotFoundException("Salle non trouvée avec le code: " + codeSalle);
                    });
            
            log.debug("Salle trouvée - Anciennes valeurs: libellé={}, contenance={}, statut={}", 
                     salle.getDescSalle(), salle.getContenance(), salle.getStatusSalle());
            
            // Validation des données de mise à jour
            if (salleDTO.getContenance() != 0 && salleDTO.getContenance() <= 0) {
                log.error("Mise à jour échouée - Contenance invalide: {} pour salle: {}", 
                         salleDTO.getContenance(), codeSalle);
                throw new IllegalArgumentException("La contenance doit être supérieure à zéro");
            }
            
            // Mettre à jour les champs
            if (salleDTO.getDescSalle() != null) {
                salle.setDescSalle(salleDTO.getDescSalle());
            }
            
            if (salleDTO.getDescSalle() != null) {
                salle.setDescSalle(salleDTO.getDescSalle());
            }
            
            if (salleDTO.getContenance() != 0) {
                salle.setContenance(salleDTO.getContenance());
            }
            
            if (salleDTO.getContenance() != 0) {
                salle.setContenance(salleDTO.getContenance());
            }
            
            if (salleDTO.getStatusSalle() != null) {
                try {
                    salle.setStatusSalle(SalleStatus.valueOf(salleDTO.getStatusSalle()));
                } catch (IllegalArgumentException e) {
                    log.error("Statut de salle invalide lors de la mise à jour: {} pour salle: {}", 
                             salleDTO.getStatusSalle(), codeSalle);
                    throw new IllegalArgumentException("Statut de salle invalide: " + 
                                                      salleDTO.getStatusSalle());
                }
            }
            
            log.debug("Nouvelles valeurs: libellé={}, contenance={}, statut={}", 
                     salle.getDescSalle(), salle.getContenance(), salle.getStatusSalle());
            
            Salle updatedSalle = salleRepository.save(salle);
            
            log.info("Salle mise à jour avec succès - code: {}, libellé: {}, statut: {}", 
                    updatedSalle.getCodeSalle(), 
                    updatedSalle.getDescSalle(),
                    updatedSalle.getStatusSalle());
            
            return salleMapper.toDTO(updatedSalle);
            
        } catch (SalleNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour de la salle: {}", codeSalle, e);
            throw new RuntimeException("Erreur lors de la mise à jour de la salle", e);
        }
    }
    
    // DELETE - Supprimer une salle
    public void deleteSalle(String codeSalle) {

        String username = currentUserProvider.getUsername();

        log.info("Tentative de suppression de la salle - code: {} l'utilisateur \"{}\"" , codeSalle, username);
        
        try {
            // Validation du code
            if (codeSalle == null || codeSalle.isEmpty()) {
                log.error("Suppression échouée - Code salle manquant");
                throw new IllegalArgumentException("Le code de la salle est obligatoire");
            }
            
            Salle salle = salleRepository.findById(codeSalle)
                    .orElseThrow(() -> {
                        log.warn("Suppression impossible - Salle non trouvée avec le code: {}", codeSalle);
                        return new SalleNotFoundException("Salle non trouvée avec le code: " + codeSalle);
                    });
            
            String libelleSalle = salle.getDescSalle(); // Pour le log après suppression
            SalleStatus statusSalle = salle.getStatusSalle();
            
            salleRepository.delete(salle);
            
            log.info("Salle supprimée avec succès - code: {}, libellé: {}, statut: {}", 
                    codeSalle, libelleSalle, statusSalle);
            
        } catch (SalleNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de la salle: {}", codeSalle, e);
            throw new RuntimeException("Erreur lors de la suppression de la salle", e);
        }
    }
    
    // Vérifier si une salle existe
    public boolean salleExists(String codeSalle) {

        String username = currentUserProvider.getUsername();

        log.debug("Vérification de l'existence de la salle: {} par \"{}\" ", codeSalle, username);
        
        try {
            // Validation du code
            if (codeSalle == null || codeSalle.isEmpty()) {
                log.error("Vérification échouée - Code salle manquant");
                throw new IllegalArgumentException("Le code de la salle est obligatoire");
            }
            
            boolean exists = salleRepository.existsById(codeSalle);
            
            log.debug("Salle {} {}", codeSalle, exists ? "existe" : "n'existe pas");
            
            return exists;
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de l'existence de la salle: {}", codeSalle, e);
            throw new RuntimeException("Erreur lors de la vérification", e);
        }
    }
    
    // Compter le nombre total de salles
    public long countSalles() {

        log.debug("Comptage du nombre total de salles");
        
        try {
            long count = salleRepository.count();
            
            log.info("Nombre total de salles dans le système: {}", count);
            
            return count;
            
        } catch (Exception e) {
            log.error("Erreur lors du comptage des salles", e);
            throw new RuntimeException("Erreur lors du comptage des salles", e);
        }
    }
}
