package com.eadl.suivi_academique.services.interfaces;

import java.util.List;

import com.eadl.suivi_academique.dto.SalleDTO;
import com.eadl.suivi_academique.utils.SalleStatus;

public interface SalleInterface {

    // CREATE - Créer une nouvelle salle
    public SalleDTO createSalle(SalleDTO salleDTO);

    // READ - Récupérer toutes les salles
    public List<SalleDTO> getAllSalles();
    // READ - Récupérer une salle par son code
    public SalleDTO getSalleByCode(String codeSalle);

    // READ - Récupérer les salles avec une contenance minimum
    public List<SalleDTO> getSallesByMinContenance(int minContenance);

    // READ - Récupérer les salles par statut
    public List<SalleDTO> getSallesByStatus(SalleStatus status);

    // UPDATE - Mettre à jour une salle
    public SalleDTO updateSalle(String codeSalle, SalleDTO salleDTO);

    // DELETE - Supprimer une salle
    public void deleteSalle(String codeSalle);


    // Vérifier si une salle existe
    public boolean salleExists(String codeSalle);

    // Compter le nombre total de salles
    public long countSalles();

}
