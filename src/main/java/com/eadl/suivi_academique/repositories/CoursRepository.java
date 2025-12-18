package com.eadl.suivi_academique.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.eadl.suivi_academique.entities.Cours;

public interface CoursRepository extends JpaRepository<Cours, String>{

    // Rechercher les cours par label (contenant le texte, insensible à la casse)
    List<Cours> findByLabelCoursContainingIgnoreCase(String labelCours);

    // Rechercher les cours avec un nombre de crédits >= minCredit
    List<Cours> findByNbCreditCoursGreaterThanEqual(int minCredit);

    // Rechercher les cours avec un nombre d'heures >= minHeures
    List<Cours> findByNbHeureCoursGreaterThanEqual(int minHeures);

    Optional <Cours> findByCodeCours(String codeCours);
}
