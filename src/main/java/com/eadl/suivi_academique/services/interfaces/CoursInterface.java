package com.eadl.suivi_academique.services.interfaces;

import java.util.List;
import com.eadl.suivi_academique.dto.CoursDTO;

public interface CoursInterface {
    
    // CREATE - Créer un nouveau cours

    public CoursDTO createCours(CoursDTO coursDTO);

    // READ - Récupérer tous les cours
    public List<CoursDTO> getAllCours();

    // READ - Récupérer un cours par son code
    public CoursDTO getCoursByCode(String codeCours);

    // READ - Récupérer les cours par label (recherche)
    public List<CoursDTO> getCoursByLabel(String labelCours);

    // UPDATE - Mettre à jour un cours
    public CoursDTO updateCours(String codeCours, CoursDTO coursDTO);

    public void deleteCours(String codeCours);

    // Vérifier si un cours existe
    public boolean coursExists(String codeCours);

    // Compter le nombre total de cours
    public long countCours();

    // Récupérer les cours avec un nombre de crédits supérieur à
    public List<CoursDTO> getCoursByMinCredit(int minCredit);

    // Récupérer les cours avec un nombre d'heures supérieur à
    public List<CoursDTO> getCoursByMinHeures(int minHeures);
}
