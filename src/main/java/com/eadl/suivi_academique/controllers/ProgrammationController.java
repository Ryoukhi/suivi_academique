package com.eadl.suivi_academique.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import com.eadl.suivi_academique.dto.ProgrammationDTO;
import com.eadl.suivi_academique.dto.ProgrammationRequest;
import com.eadl.suivi_academique.services.implementation.ProgrammationService;

@RestController
@RequestMapping("/programmations")
public class ProgrammationController {

    private final ProgrammationService programmationService;

    public ProgrammationController(ProgrammationService programmationService) {
        this.programmationService = programmationService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<ProgrammationDTO> create(@RequestBody ProgrammationRequest dto) {
        ProgrammationDTO created = programmationService.createProgrammation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ - all
    @GetMapping
    public ResponseEntity<List<ProgrammationDTO>> getAll() {
        return ResponseEntity.ok(programmationService.getAllProgrammations());
    }

    // READ - by id
    @GetMapping("/{id}")
    public ResponseEntity<ProgrammationDTO> getById(@PathVariable int id) {
        ProgrammationDTO programmation = programmationService.getProgrammationById(id);
        return ResponseEntity.ok(programmation);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ProgrammationDTO> update(
            @PathVariable int id,
            @RequestBody ProgrammationRequest dto) {

        ProgrammationDTO updated = programmationService.updateProgrammation(id, dto);
        return ResponseEntity.ok(updated);
    }

    // DELETE - by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        try {
            programmationService.deleteProgrammation(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - all
    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        programmationService.deleteAllProgrammations();
        return ResponseEntity.noContent().build();
    }
}
