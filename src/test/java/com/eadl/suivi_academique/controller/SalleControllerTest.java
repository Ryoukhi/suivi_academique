package com.eadl.suivi_academique.controller;

import com.eadl.suivi_academique.config.JwtUtil;
import com.eadl.suivi_academique.controllers.SalleController;
import com.eadl.suivi_academique.dto.SalleDTO;
import com.eadl.suivi_academique.services.implementation.SalleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SalleController.class)
@AutoConfigureMockMvc(addFilters = false)
class SalleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SalleService salleService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateSalleSuccessfully() throws Exception {
        SalleDTO salleDTO = new SalleDTO();
        salleDTO.setCodeSalle("S001");
        salleDTO.setDescSalle("Salle A");
        salleDTO.setContenance(50);
        salleDTO.setStatusSalle("LIBRE");

        when(salleService.createSalle(any(SalleDTO.class))).thenReturn(salleDTO);

        mockMvc.perform(post("/api/salles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(salleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codeSalle").value("S001"))
                .andExpect(jsonPath("$.descSalle").value("Salle A"))
                .andExpect(jsonPath("$.contenance").value(50))
                .andExpect(jsonPath("$.statusSalle").value("LIBRE"));
    }

    @Test
    void shouldGetAllSalles() throws Exception {
        SalleDTO salle1 = new SalleDTO("S001", "Salle A", 50, "LIBRE");
        SalleDTO salle2 = new SalleDTO("S002", "Salle B", 30, "LIBRE");
        List<SalleDTO> salles = Arrays.asList(salle1, salle2);

        when(salleService.getAllSalles()).thenReturn(salles);

        mockMvc.perform(get("/api/salles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].codeSalle").value("S001"))
                .andExpect(jsonPath("$[1].codeSalle").value("S002"));
    }

    @Test
    void shouldGetSalleByCode() throws Exception {
        SalleDTO salle = new SalleDTO("S001", "Salle A", 50, "LIBRE");
        when(salleService.getSalleByCode("S001")).thenReturn(salle);

        mockMvc.perform(get("/api/salles/S001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codeSalle").value("S001"))
                .andExpect(jsonPath("$.descSalle").value("Salle A"));
    }

    @Test
    void shouldUpdateSalle() throws Exception {
        SalleDTO updatedSalle = new SalleDTO("S001", "Salle A Updated", 60, "LIBRE");

        when(salleService.updateSalle(eq("S001"), any(SalleDTO.class))).thenReturn(updatedSalle);

        mockMvc.perform(put("/api/salles/S001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedSalle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descSalle").value("Salle A Updated"))
                .andExpect(jsonPath("$.contenance").value(60));
    }

    @Test
    void shouldDeleteSalle() throws Exception {
        mockMvc.perform(delete("/api/salles/S001"))
                .andExpect(status().isOk())
                .andExpect(content().string("Salle supprimée avec succès"));

        Mockito.verify(salleService).deleteSalle("S001");
    }

    @Test
    void shouldCheckSalleExists() throws Exception {
        when(salleService.salleExists("S001")).thenReturn(true);

        mockMvc.perform(get("/api/salles/exists/S001"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void shouldCountSalles() throws Exception {
        when(salleService.countSalles()).thenReturn(5L);

        mockMvc.perform(get("/api/salles/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void shouldGetSallesByMinContenance() throws Exception {
        SalleDTO salle = new SalleDTO("S003", "Grande Salle", 100, "LIBRE");
        when(salleService.getSallesByMinContenance(50)).thenReturn(List.of(salle));

        mockMvc.perform(get("/api/salles/filter/contenance/50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descSalle").value("Grande Salle"))
                .andExpect(jsonPath("$[0].contenance").value(100));
    }

    @Test
    void shouldGetSallesByStatus() throws Exception {
        SalleDTO salle = new SalleDTO("S004", "Salle Active", 40, "LIBRE");
        when(salleService.getSallesByStatus(any())).thenReturn(List.of(salle));

        mockMvc.perform(get("/api/salles/filter/status/LIBRE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descSalle").value("Salle Active"))
                .andExpect(jsonPath("$[0].statusSalle").value("LIBRE"));
    }
}