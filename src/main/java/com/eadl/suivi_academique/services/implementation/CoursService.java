package com.eadl.suivi_academique.services.implementation;

import java.util.List;
import org.springframework.stereotype.Service;
import com.eadl.suivi_academique.dto.CoursDTO;
import com.eadl.suivi_academique.entities.Cours;
import com.eadl.suivi_academique.exceptions.coursexception.CoursNotFoundException;
import com.eadl.suivi_academique.exceptions.coursexception.InvalidCoursException;
import com.eadl.suivi_academique.mappers.CoursMapper;
import com.eadl.suivi_academique.repositories.CoursRepository;
import com.eadl.suivi_academique.services.interfaces.CoursInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor // Meilleure pratique que @Autowired
public class CoursService implements CoursInterface {
    
    private final CoursRepository coursRepository;
    private final CoursMapper coursMapper;
    
    // CREATE - Créer un nouveau cours
    public CoursDTO createCours(CoursDTO coursDTO) {
        log.info("Tentative de création d'un nouveau cours - label: {}", coursDTO.getLabelCours());
        
        try {
            // Validation des données
            if (coursDTO.getLabelCours() == null || coursDTO.getLabelCours().isEmpty()) {
                log.error("Création cours échouée - Label obligatoire manquant");
                throw new InvalidCoursException("Le titre du cours est obligatoire");
            }
            
            log.debug("Validation réussie - Conversion DTO vers entité pour: {}", 
                     coursDTO.getLabelCours());
            
            // Conversion DTO vers entité
            Cours cours = coursMapper.toEntity(coursDTO);
            
            // Sauvegarde en base de données
            Cours savedCours = coursRepository.save(cours);
            
            log.info("Cours créé avec succès - code: {}, label: {}, crédits: {}, heures: {}", 
                    savedCours.getCodeCours(), 
                    savedCours.getLabelCours(),
                    savedCours.getNbCreditCours(),
                    savedCours.getNbHeureCours());
            
            // Conversion entité vers DTO
            return coursMapper.toDTO(savedCours);
            
        } catch (InvalidCoursException e) {
            log.warn("Validation échouée lors de la création du cours: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création du cours: {}", 
                     coursDTO.getLabelCours(), e);
            throw e;
        }
    }
    
    // READ - Récupérer tous les cours
    public List<CoursDTO> getAllCours() {
        log.info("Récupération de tous les cours");
        
        try {
            List<Cours> coursList = coursRepository.findAll();
            
            log.info("Nombre de cours récupérés: {}", coursList.size());
            log.debug("Conversion de {} cours en DTO", coursList.size());
            
            List<CoursDTO> coursDTO = coursList.stream()
                    .map(coursMapper::toDTO)
                    .toList();
            
            return coursDTO;
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de tous les cours", e);
            throw e;
        }
    }
    
    // READ - Récupérer un cours par son code
    public CoursDTO getCoursByCode(String codeCours) {
        log.info("Recherche du cours avec le code: {}", codeCours);
        
        try {
            // Vérification de l'existence du cours
            Cours cours = coursRepository.findById(codeCours)
                    .orElseThrow(() -> {
                        log.warn("Cours non trouvé avec le code: {}", codeCours);
                        return new CoursNotFoundException("Cours non trouvé avec le code: " + codeCours);
                    });
            
            log.info("Cours trouvé - code: {}, label: {}", cours.getCodeCours(), cours.getLabelCours());
            log.debug("Détails du cours - crédits: {}, heures: {}", 
                     cours.getNbCreditCours(), cours.getNbHeureCours());
            
            // Conversion entité vers DTO
            return coursMapper.toDTO(cours);
            
        } catch (CoursNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du cours avec le code: {}", codeCours, e);
            throw e;
        }
    }
    
    // READ - Récupérer les cours par label (recherche)
    public List<CoursDTO> getCoursByLabel(String labelCours) {
        log.info("Recherche de cours par label: {}", labelCours);
        
        try {
            List<Cours> coursList = coursRepository.findByLabelCoursContainingIgnoreCase(labelCours);
            
            log.info("Nombre de cours trouvés avec le label '{}': {}", labelCours, coursList.size());
            
            if (coursList.isEmpty()) {
                log.debug("Aucun cours trouvé pour le label: {}", labelCours);
            }
            
            return coursList.stream()
                    .map(coursMapper::toDTO)
                    .toList();
                    
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de cours par label: {}", labelCours, e);
            throw e;
        }
    }
    
    // UPDATE - Mettre à jour un cours
    public CoursDTO updateCours(String codeCours, CoursDTO coursDTO) {
        log.info("Tentative de mise à jour du cours - code: {}", codeCours);
        
        try {
            Cours cours = coursRepository.findById(codeCours)
                    .orElseThrow(() -> {
                        log.warn("Mise à jour impossible - Cours non trouvé avec le code: {}", codeCours);
                        return new CoursNotFoundException("Cours non trouvé avec le code: " + codeCours);
                    });
            
            log.debug("Cours trouvé - Anciennes valeurs: label={}, crédits={}, heures={}", 
                     cours.getLabelCours(), cours.getNbCreditCours(), cours.getNbHeureCours());
            
            // Mettre à jour les champs
            cours.setLabelCours(coursDTO.getLabelCours());
            cours.setDescCours(coursDTO.getDescCours());
            cours.setNbCreditCours(coursDTO.getNbCreditCours());
            cours.setNbHeureCours(coursDTO.getNbHeureCours());
            
            log.debug("Nouvelles valeurs: label={}, crédits={}, heures={}", 
                     coursDTO.getLabelCours(), coursDTO.getNbCreditCours(), coursDTO.getNbHeureCours());
            
            Cours updatedCours = coursRepository.save(cours);
            
            log.info("Cours mis à jour avec succès - code: {}, label: {}", 
                    updatedCours.getCodeCours(), updatedCours.getLabelCours());
            
            return coursMapper.toDTO(updatedCours);
            
        } catch (CoursNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du cours: {}", codeCours, e);
            throw e;
        }
    }
    
    // DELETE - Supprimer un cours
    public void deleteCours(String codeCours) {
        log.info("Tentative de suppression du cours - code: {}", codeCours);
        
        try {
            Cours cours = coursRepository.findById(codeCours)
                    .orElseThrow(() -> {
                        log.warn("Suppression impossible - Cours non trouvé avec le code: {}", codeCours);
                        return new CoursNotFoundException("Cours non trouvé avec le code: " + codeCours);
                    });
            
            String labelCours = cours.getLabelCours(); // Pour le log après suppression
            
            coursRepository.delete(cours);
            
            log.info("Cours supprimé avec succès - code: {}, label: {}", codeCours, labelCours);
            
        } catch (CoursNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du cours: {}", codeCours, e);
            throw e;
        }
    }
    
    // Vérifier si un cours existe
    public boolean coursExists(String codeCours) {
        log.debug("Vérification de l'existence du cours: {}", codeCours);
        
        try {
            boolean exists = coursRepository.existsById(codeCours);
            
            log.debug("Cours {} {} ", codeCours, exists ? "existe" : "n'existe pas");
            
            return exists;
            
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de l'existence du cours: {}", codeCours, e);
            throw e;
        }
    }
    
    // Compter le nombre total de cours
    public long countCours() {
        log.debug("Comptage du nombre total de cours");
        
        try {
            long count = coursRepository.count();
            
            log.info("Nombre total de cours dans le système: {}", count);
            
            return count;
            
        } catch (Exception e) {
            log.error("Erreur lors du comptage des cours", e);
            throw e;
        }
    }
    
    // Récupérer les cours avec un nombre de crédits supérieur à
    public List<CoursDTO> getCoursByMinCredit(int minCredit) {
        log.info("Recherche des cours avec un minimum de {} crédits", minCredit);
        
        try {
            List<Cours> coursList = coursRepository.findByNbCreditCoursGreaterThanEqual(minCredit);
            
            log.info("Nombre de cours trouvés avec >= {} crédits: {}", minCredit, coursList.size());
            
            if (coursList.isEmpty()) {
                log.debug("Aucun cours trouvé avec {} crédits ou plus", minCredit);
            }
            
            return coursList.stream()
                    .map(coursMapper::toDTO)
                    .toList();
                    
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de cours par crédits minimum: {}", minCredit, e);
            throw e;
        }
    }
    
    // Récupérer les cours avec un nombre d'heures supérieur à
    public List<CoursDTO> getCoursByMinHeures(int minHeures) {
        log.info("Recherche des cours avec un minimum de {} heures", minHeures);
        
        try {
            List<Cours> coursList = coursRepository.findByNbHeureCoursGreaterThanEqual(minHeures);
            
            log.info("Nombre de cours trouvés avec >= {} heures: {}", minHeures, coursList.size());
            
            if (coursList.isEmpty()) {
                log.debug("Aucun cours trouvé avec {} heures ou plus", minHeures);
            }
            
            return coursList.stream()
                    .map(coursMapper::toDTO)
                    .toList();
                    
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de cours par heures minimum: {}", minHeures, e);
            throw e;
        }
    }
}