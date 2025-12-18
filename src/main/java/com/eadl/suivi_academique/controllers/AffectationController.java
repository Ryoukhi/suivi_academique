package com.eadl.suivi_academique.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eadl.suivi_academique.dto.AffectationDTO;
import com.eadl.suivi_academique.services.implementation.AffectationService;

@RestController
@RequestMapping("/affectations")
public class AffectationController {

    @Autowired
    private  AffectationService affectationService;

    @PostMapping
    public ResponseEntity<AffectationDTO> create(@RequestBody AffectationDTO dto) {
        AffectationDTO created = affectationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<AffectationDTO>> getAll() {
        List<AffectationDTO> affectations = affectationService.getAll();
        return new ResponseEntity<>(affectations, HttpStatus.OK);
    }

    @GetMapping("/{codeCours}/{codePersonnel}")
    public ResponseEntity<AffectationDTO> getById(
            @PathVariable String codeCours,
            @PathVariable String codePersonnel) {

        AffectationDTO dto = affectationService.getById(codeCours, codePersonnel);
        return ResponseEntity.ok(dto);
    }


    @DeleteMapping("/{codeCours}/{codePersonnel}")
    public ResponseEntity<Void> delete(
            @PathVariable String codeCours,
            @PathVariable String codePersonnel) {

        affectationService.delete(codeCours, codePersonnel);
        return ResponseEntity.noContent().build();
    }
}

