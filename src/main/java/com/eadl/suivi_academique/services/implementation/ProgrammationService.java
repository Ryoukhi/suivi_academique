package com.eadl.suivi_academique.services.implementation;

import com.eadl.suivi_academique.dto.ProgrammationDTO;
import com.eadl.suivi_academique.dto.ProgrammationRequest;
import com.eadl.suivi_academique.entities.*;
import com.eadl.suivi_academique.exceptions.coursexception.CoursNotFoundException;
import com.eadl.suivi_academique.exceptions.personnelexception.PersonnelNotFoundException;
import com.eadl.suivi_academique.exceptions.programmationexception.ProgrammationNotFoundException;
import com.eadl.suivi_academique.exceptions.salleexception.SalleIndisponibleException;
import com.eadl.suivi_academique.exceptions.salleexception.SalleNotFoundException;
import com.eadl.suivi_academique.mappers.ProgrammationMapper;
import com.eadl.suivi_academique.mappers.ProgrammationReqMapper;
import com.eadl.suivi_academique.repositories.*;
import com.eadl.suivi_academique.services.interfaces.ProgrammationInterface;
import com.eadl.suivi_academique.utils.StatusProgrammation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProgrammationService implements ProgrammationInterface {

    private final ProgrammationRepository programmationRepository;
    private final ProgrammationMapper programmationMapper;
    private final ProgrammationReqMapper programmationReqMapper;
    private final SalleRepository salleRepository;
    private final CoursRepository coursRepository;
    private final PersonnelRepository personnelRepository;

    @Override
    public ProgrammationDTO createProgrammation(ProgrammationRequest dto) {
        log.info("Création d'une programmation - Cours: {}, Salle: {}", dto.getCodeCours(), dto.getCodeSalle());

        validateProgrammationDates(dto);
        Salle salle = fetchSalleAndVerifyAvailability(dto.getCodeSalle());

        Programmation programmation = programmationReqMapper.toEntity(dto);
        // Le mapper gère normalement les associations, sinon on les réassigne ici
        
        return programmationMapper.toDTO(programmationRepository.save(programmation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgrammationDTO> getAllProgrammations() {
        return programmationMapper.tDtos(programmationRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ProgrammationDTO getProgrammationById(int id) {
        Assert.isTrue(id > 0, "L'ID doit être positif");
        return programmationRepository.findById(id)
                .map(programmationMapper::toDTO)
                .orElseThrow(() -> new ProgrammationNotFoundException("Programmation introuvable : " + id));
    }

    @Override
    public ProgrammationDTO updateProgrammation(int id, ProgrammationRequest dto) {
        log.info("Mise à jour programmation ID : {}", id);
        Assert.isTrue(id > 0, "L'ID doit être positif");
        validateProgrammationDates(dto);

        return programmationRepository.findById(id)
                .map(existing -> {
                    updateProgrammationFields(existing, dto);
                    return programmationMapper.toDTO(programmationRepository.save(existing));
                })
                .orElseThrow(() -> new ProgrammationNotFoundException("Impossible de mettre à jour : ID " + id + " inconnu"));
    }

    @Override
    public void deleteProgrammation(int id) {
        log.warn("Suppression programmation ID : {}", id);
        if (!programmationRepository.existsById(id)) {
            throw new ProgrammationNotFoundException("Suppression impossible : ID " + id + " inconnu");
        }
        programmationRepository.deleteById(id);
    }

    @Override
    public void deleteAllProgrammations() {
        log.error("Suppression de TOUTES les programmations");
        programmationRepository.deleteAll();
    }

    // --- Méthodes privées de support ---

    private void validateProgrammationDates(ProgrammationRequest dto) {
        Assert.notNull(dto.getDebutProgammation(), "Date de début obligatoire");
        Assert.notNull(dto.getFinProgammation(), "Date de fin obligatoire");
        if (dto.getFinProgammation().before(dto.getDebutProgammation())) {
            throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début");
        }
    }

    private Salle fetchSalleAndVerifyAvailability(String codeSalle) {
        Salle salle = salleRepository.findByCodeSalle(codeSalle)
                .orElseThrow(() -> new SalleNotFoundException("Salle introuvable : " + codeSalle));

        String status = String.valueOf(salle.getStatusSalle());
        if ("FERMEE".equals(status) || "OCCUPEE".equals(status)) {
            throw new SalleIndisponibleException("La salle " + codeSalle + " est actuellement " + status);
        }
        return salle;
    }

    private void updateProgrammationFields(Programmation entity, ProgrammationRequest dto) {
        entity.setNbHeureProgammation(dto.getNbHeureProgammation());
        entity.setDebutProgammation(dto.getDebutProgammation());
        entity.setFinProgammation(dto.getFinProgammation());
        
        // Mise à jour des relations
        entity.setCours(coursRepository.findByCodeCours(dto.getCodeCours())
                .orElseThrow(() -> new CoursNotFoundException("Cours introuvable : " + dto.getCodeCours())));
        
        entity.setPersonnelProg(personnelRepository.findByCodePersonnel(dto.getCodePersonnelProg())
                .orElseThrow(() -> new PersonnelNotFoundException("Programmateur introuvable")));
        
        entity.setPersonnelVal(personnelRepository.findByCodePersonnel(dto.getCodePersonnelVal())
                .orElseThrow(() -> new PersonnelNotFoundException("Validateur introuvable")));

        try {
            entity.setStatusProgrammation(StatusProgrammation.valueOf(dto.getStatusProgrammation()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Statut invalide : " + dto.getStatusProgrammation());
        }
    }
}