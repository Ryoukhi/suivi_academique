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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eadl.suivi_academique.dto.CoursDTO;
import com.eadl.suivi_academique.services.implementation.CoursService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/cours")
public class CoursController {

    @Autowired
    private CoursService coursService;

    // CREATE - Créer un nouveau cours
    @PostMapping
    @Operation(summary = "Créer Cours", description = "Créer un nouveau cours")
    public CoursDTO createCours(@RequestBody CoursDTO coursDTO) {
        return coursService.createCours(coursDTO);
    }

    // READ - Récupérer tous les cours
    @GetMapping
    @Operation(summary = "Afficher Cours", description = "Afficher tous les cours")
    public ResponseEntity<List<CoursDTO>> getAllCours() {
        List<CoursDTO> coursList = coursService.getAllCours();
        return new ResponseEntity<>(coursList, HttpStatus.OK);
    }

    // READ - Récupérer un cours par son code
    @GetMapping("/{codeCours}")
    @Operation(summary = "Chercher cours", description = "Chercher un cours par son code")
    public CoursDTO getCoursByCode(@PathVariable String codeCours) {

        return coursService.getCoursByCode(codeCours);
    }

    // READ - Rechercher les cours par label
    @GetMapping("/search/label")
    @Operation(summary = "Rechercher cours", description = "Rechercher les cours par label")
    public ResponseEntity<List<CoursDTO>> getCoursByLabel(@RequestParam String labelCours) {
        List<CoursDTO> coursList = coursService.getCoursByLabel(labelCours);
        return new ResponseEntity<>(coursList, HttpStatus.OK);
    }

    // READ - Récupérer les cours avec un nombre de crédits minimum
    @GetMapping("/filter/credit/{minCredit}")
    @Operation(summary = "Rechercher cours", description = "Rechercher les cours avec un nombre de crédits minimum")
    public ResponseEntity<List<CoursDTO>> getCoursByMinCredit(@PathVariable int minCredit) {
        List<CoursDTO> coursList = coursService.getCoursByMinCredit(minCredit);
        return new ResponseEntity<>(coursList, HttpStatus.OK);
    }

    // READ - Récupérer les cours avec un nombre d'heures minimum
    @GetMapping("/filter/heures/{minHeures}")
    @Operation(summary = "Rechercher cours", description = "Rechercher les cours avec un nombre d'heures minimum")
    public ResponseEntity<List<CoursDTO>> getCoursByMinHeures(@PathVariable int minHeures) {
        List<CoursDTO> coursList = coursService.getCoursByMinHeures(minHeures);
        return new ResponseEntity<>(coursList, HttpStatus.OK);
    }

    // UPDATE - Mettre à jour un cours
    @PutMapping("/{codeCours}")
    @Operation(summary = "Modifier cours", description = "Modifier les informations d'un cours")
    public ResponseEntity<CoursDTO> updateCours(@PathVariable String codeCours,
            @RequestBody CoursDTO coursDTO) {
        CoursDTO updatedCours = coursService.updateCours(codeCours, coursDTO);
        return new ResponseEntity<>(updatedCours, HttpStatus.OK);
    }

    // DELETE - Supprimer un cours
    @DeleteMapping("/{codeCours}")
    @Operation(summary = "Supprrimer cours", description = "Supprimer un cours par son cide")
    public ResponseEntity<String> deleteCours(@PathVariable String codeCours) {
        coursService.deleteCours(codeCours);
        return new ResponseEntity<>("Cours supprimé avec succès", HttpStatus.OK);
    }

    // Vérifier si un cours existe
    @GetMapping("/exists/{codeCours}")
    @Operation(summary = "Verifier existance cours", description = "Verifier si un cours existe par son code")
    public ResponseEntity<Boolean> coursExists(@PathVariable String codeCours) {
        boolean exists = coursService.coursExists(codeCours);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    // Compter le nombre total de cours
    @GetMapping("/count")
    @Operation(summary = "Compter nombre cours", description = "Compter le nombre total de cours")
    public ResponseEntity<Long> countCours() {
        long count = coursService.countCours();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}
