package com.eadl.suivi_academique.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eadl.suivi_academique.entities.Personnel;

public interface PersonnelRepository extends JpaRepository<Personnel, String> {

    List<Personnel> findByNomPersonnel(String nomPersonnel);

    @Query("SELECT p FROM Personnel p WHERE p.nomPersonnel LIKE '%:token%'")
    List<Personnel> searchByName(@Param("token") String token);

    @Query(value = "SELECT count(*) FROM personnel p WHERE p.sexe_personnel = :sexe", nativeQuery = true)
    int countBysexe(@Param("sexe") String sexe);

    Personnel findByRolePersonnel(String rolePersonnel);

    Optional<Personnel> findByCodePersonnel(String codePersonnel);

    Optional<Personnel> findByLoginPersonnel(String loginPersonnel);

    boolean existsByLoginPersonnel(String loginPersonnel);


}
