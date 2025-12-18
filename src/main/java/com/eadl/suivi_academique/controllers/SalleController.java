package com.eadl.suivi_academique.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eadl.suivi_academique.dto.SalleDTO;
import com.eadl.suivi_academique.services.implementation.SalleService;
import com.eadl.suivi_academique.utils.SalleStatus;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/salles")
public class SalleController {

    @Autowired
    private SalleService salleService;

    
    // CREATE - Créer une nouvelle salle
    @PostMapping
    @Operation(summary = "Créer Salle", description = "Créer une nouvelle salle")
    public SalleDTO createSalle(@RequestBody SalleDTO salleDTO) {
        return salleService.createSalle(salleDTO);
    }

    // READ - Récupérer toutes les salles
    @GetMapping
    @Operation(summary = "Afficher Salles", description = "Afficher toutes les salles")
    public ResponseEntity<List<SalleDTO>> getAllSalles() {
        List<SalleDTO> salles = salleService.getAllSalles();
        return new ResponseEntity<>(salles, HttpStatus.OK);
    }

    // READ - Récupérer une salle par son code
    @GetMapping("/{codeSalle}")
    @Operation(summary = "Chercher salle", description = "Chercher salle par son code")
    public ResponseEntity<SalleDTO> getSalleByCode(@PathVariable String codeSalle) {
        SalleDTO salle = salleService.getSalleByCode(codeSalle);
        return new ResponseEntity<>(salle, HttpStatus.OK);
    }

    // READ - Récupérer les salles avec une contenance minimum
    @GetMapping("/filter/contenance/{minContenance}")
    @Operation(summary = "Chercher salles", description = "Chercher les salles avec contenance minimum")
    public ResponseEntity<List<SalleDTO>> getSallesByMinContenance(@PathVariable int minContenance) {
        List<SalleDTO> salles = salleService.getSallesByMinContenance(minContenance);
        return new ResponseEntity<>(salles, HttpStatus.OK);
    }

    // READ - Récupérer les salles par statut
    @GetMapping("/filter/status/{status}")
    @Operation(summary = "Afficher salles", description = "Afficher les salles en fonction du statut")
    public ResponseEntity<List<SalleDTO>> getSallesByStatus(@PathVariable String status) {
        List<SalleDTO> salles = salleService.getSallesByStatus(SalleStatus.valueOf(status));
        return new ResponseEntity<>(salles, HttpStatus.OK);
    }

    // UPDATE - Mettre à jour une salle
    @PutMapping("/{codeSalle}")
    @Operation(summary = "Modifier salle", description = "Modifier les informations d'une salle")
    public ResponseEntity<SalleDTO> updateSalle(@PathVariable String codeSalle,
            @RequestBody SalleDTO salleDTO) {
        SalleDTO updatedSalle = salleService.updateSalle(codeSalle, salleDTO);
        return new ResponseEntity<>(updatedSalle, HttpStatus.OK);
    }

    // DELETE - Supprimer une salle
    @DeleteMapping("/{codeSalle}")
    @Operation(summary = "Supprimer salle", description = "Supprimer une salle par son code")
    public ResponseEntity<String> deleteSalle(@PathVariable String codeSalle) {
        salleService.deleteSalle(codeSalle);
        return new ResponseEntity<>("Salle supprimée avec succès", HttpStatus.OK);
    }

    // Vérifier si une salle existe
    @GetMapping("/exists/{codeSalle}")
    @Operation(summary = "Vérifier salle", description = "Vérifier si une salle existe par son code")
    public ResponseEntity<Boolean> salleExists(@PathVariable String codeSalle) {
        boolean exists = salleService.salleExists(codeSalle);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    // Compter le nombre total de salles
    @GetMapping("/count")
    @Operation(summary = "Compter salles", description = "Compter le nombre total de salles")
    public ResponseEntity<Long> countSalles() {
        long count = salleService.countSalles();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

}
