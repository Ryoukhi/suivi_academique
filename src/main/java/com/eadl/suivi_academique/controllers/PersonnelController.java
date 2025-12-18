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

import com.eadl.suivi_academique.dto.PersonnelDTO;
import com.eadl.suivi_academique.services.implementation.PersonnelService;

@RestController
@RequestMapping("/api/personnels")
public class PersonnelController {

    @Autowired
    private PersonnelService personnelService;

    // CREATE - Créer un nouveau personnel
    @PostMapping
    public ResponseEntity<PersonnelDTO> createPersonnel(@RequestBody PersonnelDTO personnelDTO) {
        PersonnelDTO createdPersonnel = personnelService.createPersonnel(personnelDTO);
        return new ResponseEntity<>(createdPersonnel, HttpStatus.CREATED);
    }

    // READ - Récupérer tous les personnels
    @GetMapping
    public ResponseEntity<List<PersonnelDTO>> getAllPersonnels() {
        List<PersonnelDTO> personnels = personnelService.getAllPersonnels();
        return new ResponseEntity<>(personnels, HttpStatus.OK);
    }

    // READ - Récupérer un personnel par son code
    @GetMapping("/{codePersonnel}")
    public ResponseEntity<PersonnelDTO> getPersonnelByCode(@PathVariable String codePersonnel) {
        PersonnelDTO personnel = personnelService.getPersonnelByCode(codePersonnel);
        return new ResponseEntity<>(personnel, HttpStatus.OK);
    }

    // UPDATE - Mettre à jour un personnel
    @PutMapping("/{codePersonnel}")
    public ResponseEntity<PersonnelDTO> updatePersonnel(@PathVariable String codePersonnel,
            @RequestBody PersonnelDTO personnelDTO) {
        PersonnelDTO updatedPersonnel = personnelService.updatePersonnel(codePersonnel, personnelDTO);
        return new ResponseEntity<>(updatedPersonnel, HttpStatus.OK);
    }

    // DELETE - Supprimer un personnel
    @DeleteMapping("/{codePersonnel}")
    public ResponseEntity<String> deletePersonnel(@PathVariable String codePersonnel) {
        personnelService.deletePersonnel(codePersonnel);
        return new ResponseEntity<>("Personnel supprimé avec succès", HttpStatus.OK);
    }

    // DELETE - Supprimer tous les personnels
    @DeleteMapping
    public ResponseEntity<String> deleteAllPersonnels() {
        personnelService.deleteAllPersonnels();
        return new ResponseEntity<>("Tous les personnels ont été supprimés", HttpStatus.OK);
    }

    // Vérifier si un personnel existe
    @GetMapping("/exists/{codePersonnel}")
    public ResponseEntity<Boolean> personnelExists(@PathVariable String codePersonnel) {
        boolean exists = personnelService.personnelExists(codePersonnel);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    // Compter le nombre total de personnels
    @GetMapping("/count")
    public ResponseEntity<Long> countPersonnels() {
        long count = personnelService.countPersonnels();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}
