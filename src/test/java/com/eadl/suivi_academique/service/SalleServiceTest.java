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
import com.eadl.suivi_academique.config.RequestContextProvider;
import com.eadl.suivi_academique.config.CurrentUserProvider;
import com.eadl.suivi_academique.dto.SalleDTO;
import com.eadl.suivi_academique.entities.Salle;
import com.eadl.suivi_academique.exceptions.salleexception.InvalidSalleException;
import com.eadl.suivi_academique.mappers.SalleMapper;
import com.eadl.suivi_academique.repositories.SalleRepository;
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

    @Mock
    private RequestContextProvider requestContextProvider;

    @Mock
    private CurrentUserProvider currentUserProvider;

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

        // ⚡ Mock des dépendances qui causent le NPE
        when(currentUserProvider.getUsername()).thenReturn("admin");
        when(requestContextProvider.getClientIp()).thenReturn("127.0.0.1");
        when(requestContextProvider.getUserAgent()).thenReturn("JUnit-Test");

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
        assertEquals("LIBRE", result.getStatusSalle());

        // Vérifications des interactions avec les mocks
        verify(currentUserProvider).getUsername();
        verify(requestContextProvider).getClientIp();
        verify(requestContextProvider).getUserAgent();
        verify(salleRepository).existsById("S001");
        verify(salleRepository).save(salle);
        verify(salleMapper).toDTO(salle);
    }

    @Test
    void should_throw_InvalidSalleException_when_code_salle_is_missing() {
        // GIVEN
        SalleDTO dto = new SalleDTO();
        // codeSalle manquant
        dto.setDescSalle("Salle Informatique");
        dto.setStatusSalle("LIBRE");
        dto.setContenance(30);

        // ⚡ Mock des dépendances qui causent le NPE
        when(currentUserProvider.getUsername()).thenReturn("admin");
        when(requestContextProvider.getClientIp()).thenReturn("127.0.0.1");
        when(requestContextProvider.getUserAgent()).thenReturn("JUnit-Test");

        // WHEN 
        InvalidSalleException exception = assertThrows(
                InvalidSalleException.class,
                () -> salleService.createSalle(dto)
        );

        // THEN
        assertEquals("Le code de la salle est obligatoire", exception.getMessage());

        // Vérifier que salleRepository et salleMapper ne sont pas utilisés
        verifyNoInteractions(salleRepository);
        verifyNoInteractions(salleMapper);
        verify(currentUserProvider).getUsername();
        verify(requestContextProvider).getClientIp();
        verify(requestContextProvider).getUserAgent();
    }

}
