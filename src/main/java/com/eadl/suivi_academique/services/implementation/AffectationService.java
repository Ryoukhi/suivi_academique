package com.eadl.suivi_academique.services.implementation;

import com.eadl.suivi_academique.dto.AffectationDTO;
import com.eadl.suivi_academique.entities.*;
import com.eadl.suivi_academique.exceptions.affectationexception.AffectationNotFoundException;
import com.eadl.suivi_academique.exceptions.coursexception.CoursNotFoundException;
import com.eadl.suivi_academique.exceptions.personnelexception.PersonnelNotFoundException;
import com.eadl.suivi_academique.mappers.AffectationMapper;
import com.eadl.suivi_academique.repositories.AffectationRepository;
import com.eadl.suivi_academique.repositories.CoursRepository;
import com.eadl.suivi_academique.repositories.PersonnelRepository;
import com.eadl.suivi_academique.services.interfaces.AffectationInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional // Assure l'intégrité des données
public class AffectationService implements AffectationInterface {

    private final AffectationRepository affectationRepository;
    private final PersonnelRepository personnelRepository;
    private final CoursRepository coursRepository;
    private final AffectationMapper affectationMapper;

    // Le code actuel etait fonctionnel, mais il souffrait de ce qu'on appelle la "Fat Service Layer" : les méthodes était longues, contennaient trop de blocs try-catch répétitifs et mélangeaient la logique métier avec la validation technique.

    @Override
    public AffectationDTO create(AffectationDTO dto) {
        log.info("Création affectation : Personnel {} -> Cours {}", dto.getCodePersonnel(), dto.getCodeCours());

        validateCreateRequest(dto);

        // 1. Vérifier l'existence des entités parentes
        Personnel personnel = personnelRepository.findById(dto.getCodePersonnel())
                .orElseThrow(() -> new PersonnelNotFoundException("Personnel introuvable: " + dto.getCodePersonnel()));

        Cours cours = coursRepository.findById(dto.getCodeCours())
                .orElseThrow(() -> new CoursNotFoundException("Cours introuvable: " + dto.getCodeCours()));

        // 2. Création et sauvegarde
        AffectationId id = new AffectationId(cours.getCodeCours(), personnel.getCodePersonnel());
        
        Affectation aff = new Affectation();
        aff.setCodeAffectation(id);
        aff.setPersonnel(personnel);
        aff.setCours(cours);

        Affectation saved = affectationRepository.save(aff);
        log.info("Affectation créée avec succès.");

        return affectationMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AffectationDTO> getAll() {
        log.debug("Récupération de toutes les affectations");
        return affectationMapper.toDtos(affectationRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public AffectationDTO getById(String codeCours, String codePersonnel) {
        Assert.hasText(codeCours, "Le code cours est requis");
        Assert.hasText(codePersonnel, "Le code personnel est requis");

        return affectationRepository.findById(new AffectationId(codeCours, codePersonnel))
                .map(affectationMapper::toDTO)
                .orElseThrow(() -> new AffectationNotFoundException(
                        String.format("Affectation non trouvée pour Personnel %s et Cours %s", codePersonnel, codeCours)));
    }

    @Override
    public void delete(String codeCours, String codePersonnel) {
        log.info("Suppression affectation : Personnel {} -> Cours {}", codePersonnel, codeCours);
        
        AffectationId id = new AffectationId(codeCours, codePersonnel);
        
        if (!affectationRepository.existsById(id)) {
            throw new AffectationNotFoundException("Impossible de supprimer : l'affectation n'existe pas.");
        }

        affectationRepository.deleteById(id);
        log.info("Affectation supprimée.");
    }

    /**
     * Centralisation des validations métier
     */
    private void validateCreateRequest(AffectationDTO dto) {
        Assert.hasText(dto.getCodePersonnel(), "Le code personnel est obligatoire");
        Assert.hasText(dto.getCodeCours(), "Le code cours est obligatoire");

        AffectationId id = new AffectationId(dto.getCodeCours(), dto.getCodePersonnel());
        if (affectationRepository.existsById(id)) {
            throw new IllegalArgumentException("Cette affectation existe déjà");
        }
    }
}