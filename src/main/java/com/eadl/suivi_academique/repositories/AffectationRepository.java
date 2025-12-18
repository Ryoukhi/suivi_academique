package com.eadl.suivi_academique.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eadl.suivi_academique.entities.Affectation;
import com.eadl.suivi_academique.entities.AffectationId;

public interface AffectationRepository extends JpaRepository<Affectation, AffectationId> {

    // Find all affectations by personnel code
    @Query("SELECT a FROM Affectation a WHERE a.personnel.codePersonnel = :codePersonnel")
    List<Affectation> findByPersonnelCode(@Param("codePersonnel") String codePersonnel);

    // Find all affectations by cours code
    @Query("SELECT a FROM Affectation a WHERE a.cours.codeCours = :codeCours")
    List<Affectation> findByCoursCode(@Param("codeCours") String codeCours);

    // Find affectation by personnel and cours codes
    @Query("SELECT a FROM Affectation a WHERE a.personnel.codePersonnel = :codePersonnel AND a.cours.codeCours = :codeCours")
    Optional<Affectation> findByPersonnelAndCoursCode(@Param("codePersonnel") String codePersonnel, @Param("codeCours") String codeCours);

    // Count affectations by personnel
    @Query("SELECT COUNT(a) FROM Affectation a WHERE a.personnel.codePersonnel = :codePersonnel")
    long countByPersonnelCode(@Param("codePersonnel") String codePersonnel);

    // Count affectations by cours
    @Query("SELECT COUNT(a) FROM Affectation a WHERE a.cours.codeCours = :codeCours")
    long countByCoursCode(@Param("codeCours") String codeCours);

}
