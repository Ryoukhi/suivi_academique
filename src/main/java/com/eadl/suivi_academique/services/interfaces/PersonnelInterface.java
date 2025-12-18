package com.eadl.suivi_academique.services.interfaces;

import java.util.List;

import com.eadl.suivi_academique.dto.PersonnelDTO;

public interface PersonnelInterface {

    // CREATE - Créer un nouveau personnel
    public PersonnelDTO createPersonnel(PersonnelDTO personnelDTO);

    // READ - Récupérer tous les personnels
    public List<PersonnelDTO> getAllPersonnels();

    //Recuperer un personnel par son role
    public PersonnelDTO getPersonnelByRole(String rolePersonnel);

    // READ - Récupérer un personnel par son code
    public PersonnelDTO getPersonnelByCode(String codePersonnel);

    // UPDATE - Mettre à jour un personnel
    public PersonnelDTO updatePersonnel(String codePersonnel, PersonnelDTO personnelDTO);
    // DELETE - Supprimer un personnel
    public void deletePersonnel(String codePersonnel);

    // DELETE - Supprimer tous les personnels
    public void deleteAllPersonnels();
    // Vérifier si un personnel existe
    public boolean personnelExists(String codePersonnel);
    // Compter le nombre total de personnels
    public long countPersonnels();
}
