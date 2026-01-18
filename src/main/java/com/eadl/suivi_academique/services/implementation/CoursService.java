package com.eadl.suivi_academique.services.implementation;

import com.eadl.suivi_academique.dto.CoursDTO;
import com.eadl.suivi_academique.entities.Cours;
import com.eadl.suivi_academique.exceptions.coursexception.CoursNotFoundException;
import com.eadl.suivi_academique.exceptions.coursexception.InvalidCoursException;
import com.eadl.suivi_academique.mappers.CoursMapper;
import com.eadl.suivi_academique.repositories.CoursRepository;
import com.eadl.suivi_academique.services.interfaces.CoursInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CoursService implements CoursInterface {

    private final CoursRepository coursRepository;
    private final CoursMapper coursMapper;

    @Override
    public CoursDTO createCours(CoursDTO dto) {
        log.info("Création d'un cours : {}", dto.getLabelCours());
        
        validateCoursDTO(dto);
        
        Cours cours = coursMapper.toEntity(dto);
        Cours saved = coursRepository.save(cours);
        
        return coursMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoursDTO> getAllCours() {
        return coursRepository.findAll().stream()
                .map(coursMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CoursDTO getCoursByCode(String codeCours) {
        return coursRepository.findById(codeCours)
                .map(coursMapper::toDTO)
                .orElseThrow(() -> new CoursNotFoundException("Cours introuvable avec le code : " + codeCours));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoursDTO> getCoursByLabel(String labelCours) {
        return coursRepository.findByLabelCoursContainingIgnoreCase(labelCours).stream()
                .map(coursMapper::toDTO)
                .toList();
    }

    @Override
    public CoursDTO updateCours(String codeCours, CoursDTO dto) {
        log.info("Mise à jour du cours code : {}", codeCours);
        
        validateCoursDTO(dto);

        return coursRepository.findById(codeCours)
                .map(existingCours -> {
                    updateFields(existingCours, dto);
                    return coursMapper.toDTO(coursRepository.save(existingCours));
                })
                .orElseThrow(() -> new CoursNotFoundException("Cours introuvable : " + codeCours));
    }

    @Override
    public void deleteCours(String codeCours) {
        log.info("Suppression du cours code : {}", codeCours);
        
        if (!coursRepository.existsById(codeCours)) {
            throw new CoursNotFoundException("Impossible de supprimer : cours introuvable");
        }
        coursRepository.deleteById(codeCours);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean coursExists(String codeCours) {
        return coursRepository.existsById(codeCours);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCours() {
        return coursRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoursDTO> getCoursByMinCredit(int minCredit) {
        return coursRepository.findByNbCreditCoursGreaterThanEqual(minCredit).stream()
                .map(coursMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoursDTO> getCoursByMinHeures(int minHeures) {
        return coursRepository.findByNbHeureCoursGreaterThanEqual(minHeures).stream()
                .map(coursMapper::toDTO)
                .toList();
    }

    // --- Méthodes privées utilitaires ---

    private void validateCoursDTO(CoursDTO dto) {
        if (dto.getLabelCours() == null || dto.getLabelCours().isBlank()) {
            throw new InvalidCoursException("Le titre du cours est obligatoire");
        }
        // Ajoutez d'autres validations ici si nécessaire
    }

    private void updateFields(Cours existing, CoursDTO dto) {
        existing.setLabelCours(dto.getLabelCours());
        existing.setDescCours(dto.getDescCours());
        existing.setNbCreditCours(dto.getNbCreditCours());
        existing.setNbHeureCours(dto.getNbHeureCours());
    }
}