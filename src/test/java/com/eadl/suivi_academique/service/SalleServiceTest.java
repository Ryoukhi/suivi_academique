package com.eadl.suivi_academique.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eadl.suivi_academique.dto.SalleDTO;
import com.eadl.suivi_academique.entities.Salle;
import com.eadl.suivi_academique.mappers.SalleMapper;
import com.eadl.suivi_academique.repositories.SalleRepository;
import com.eadl.suivi_academique.services.exceptions.salleexception.InvalidSalleException;
import com.eadl.suivi_academique.services.implementation.SalleService;
import com.eadl.suivi_academique.utils.SalleStatus;

@ExtendWith(MockitoExtension.class)
public class SalleServiceTest {

    @Mock
    private SalleRepository salleRepository;

    @Mock
    private SalleMapper salleMapper;

    @InjectMocks
    private SalleService salleService;

    @Test
    void should_create_salle_successfully() {
        // GIVEN (préparation)
        SalleDTO dto = new SalleDTO();
        dto.setCodeSalle("S001");
        dto.setDescSalle("Salle Informatique");
        dto.setStatusSalle("LIBRE");
        dto.setContenance(30);

        Salle salle = new Salle();
        salle.setCodeSalle("S001");
        salle.setDescSalle("Salle Informatique");
        salle.setStatusSalle(SalleStatus.LIBRE);
        salle.setContenance(30);

        when(salleRepository.existsById("S001")).thenReturn(false);
        when(salleMapper.toEntity(dto)).thenReturn(salle);
        when(salleRepository.save(salle)).thenReturn(salle);
        when(salleMapper.toDTO(salle)).thenReturn(dto);

        // WHEN (action)
        SalleDTO result = salleService.createSalle(dto);

        // THEN (vérifications)
        assertNotNull(result);
        assertEquals("S001", result.getCodeSalle());
        assertEquals("Salle Informatique", result.getDescSalle());

        verify(salleRepository).existsById("S001");
        verify(salleRepository).save(salle);
    }

    @Test
    void should_throw_InvalidSalleException_when_code_salle_is_missing() {
        // GIVEN
        SalleDTO dto = new SalleDTO();
        //dto.setCodeSalle("S001");
        dto.setDescSalle("Salle Informatique");
        dto.setStatusSalle("LIBRE");
        dto.setContenance(30);

        // WHEN 
        InvalidSalleException exception = assertThrows(
                InvalidSalleException.class,
                () -> salleService.createSalle(dto)
        );

        // THEN
        assertEquals("Le code de la salle est obligatoire", exception.getMessage());

        verifyNoInteractions(salleRepository);
        verifyNoInteractions(salleMapper);
    }



}
