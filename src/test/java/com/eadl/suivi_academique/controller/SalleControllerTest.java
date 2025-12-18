package com.eadl.suivi_academique.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.eadl.suivi_academique.config.JwtAuthenticationFilter;
import com.eadl.suivi_academique.config.JwtUtil;
import com.eadl.suivi_academique.controllers.SalleController;
import com.eadl.suivi_academique.dto.SalleDTO;
import com.eadl.suivi_academique.services.exceptions.salleexception.InvalidSalleException;
import com.eadl.suivi_academique.services.exceptions.salleexception.SalleNotFoundException;
import com.eadl.suivi_academique.services.implementation.SalleService;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(SalleController.class)
@AutoConfigureMockMvc(addFilters = false) // ⚡ active/désactive tous les filtres Spring Security
public class SalleControllerTest {

    @MockBean
    private SalleService salleService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_create_salle_successfully() throws Exception {

        // GIVEN
        SalleDTO dto = new SalleDTO();
        dto.setCodeSalle("S001");
        dto.setDescSalle("Salle Info");
        dto.setStatusSalle("LIBRE");
        dto.setContenance(30);

        when(salleService.createSalle(any(SalleDTO.class)))
                .thenReturn(dto);

        // WHEN + THEN
        mockMvc.perform(post("/api/salles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.codeSalle").value("S001"))
            .andExpect(jsonPath("$.descSalle").value("Salle Info"));
    }

    @Test
    void should_update_salle_successfully() throws Exception {

        // GIVEN
        SalleDTO dto = new SalleDTO();
        dto.setCodeSalle("S001");
        dto.setDescSalle("Salle Mise à Jour");
        dto.setStatusSalle("OCCUPEE");
        dto.setContenance(40);

        when(salleService.createSalle(any(SalleDTO.class)))
                .thenReturn(dto);

        // WHEN + THEN
        mockMvc.perform(post("/api/salles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.codeSalle").value("S001"))
            .andExpect(jsonPath("$.descSalle").value("Salle Mise à Jour"));
    }

    @Test
    void should_delete_salle_successfully() throws Exception {

        // GIVEN
        String codeSalle = "S001";

        // WHEN + THEN
        mockMvc.perform(delete("/api/salles/{codeSalle}", codeSalle))
            .andExpect(status().isOk());
    }

    @Test
    void should_return_400_when_code_salle_is_missing() throws Exception {

        // GIVEN
        SalleDTO dto = new SalleDTO();
        dto.setDescSalle("Salle Test");
        dto.setStatusSalle("DISPONIBLE");

        when(salleService.createSalle(any(SalleDTO.class)))
                .thenThrow(new InvalidSalleException("Le code de la salle est obligatoire"));

        // WHEN + THEN
        mockMvc.perform(post("/api/salles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_all_salles() throws Exception {

        // GIVEN
        SalleDTO salle1 = new SalleDTO();
        salle1.setCodeSalle("S001");

        SalleDTO salle2 = new SalleDTO();
        salle2.setCodeSalle("S002");

        when(salleService.getAllSalles())
                .thenReturn(List.of(salle1, salle2));

        // WHEN + THEN
        mockMvc.perform(get("/api/salles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].codeSalle").value("S001"));
    }

    @Test
    void should_return_salle_by_code() throws Exception {

        // GIVEN
        SalleDTO dto = new SalleDTO();
        dto.setCodeSalle("S001");

        when(salleService.getSalleByCode("S001"))
                .thenReturn(dto);

        // WHEN + THEN
        mockMvc.perform(get("/api/salles/S001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.codeSalle").value("S001"));
    }

    @Test
    void should_return_404_when_salle_not_found() throws Exception {

        when(salleService.getSalleByCode("S999"))
                .thenThrow(new SalleNotFoundException("Salle introuvable"));

        mockMvc.perform(get("/api/salles/S999"))
            .andExpect(status().isNotFound());
    }

}
