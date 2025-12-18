package com.eadl.suivi_academique.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eadl.suivi_academique.entities.Salle;
import com.eadl.suivi_academique.utils.SalleStatus;

@Repository
public interface SalleRepository extends JpaRepository<Salle, String> {

    // existing convenience methods
    boolean existsByContenance(int contenance);

    List<Salle> findByContenanceGreaterThanEquals(int contenance);
    
    Optional<Salle> findByCodeSalle(String codeSalle);

    // Methods that call the NamedQueries defined on the Salle entity
    @Query(name = "Salle.findAll")
    List<Salle> findAllByNamed();

    @Query(name = "Salle.findByCodeSalle")
    Optional<Salle> findByCodeSalleNamed(@Param("codeSalle") String codeSalle);

    @Query(name = "Salle.findByContenanceGreaterThanEquals")
    List<Salle> findByContenanceGreaterThanEqualsNamed(@Param("contenance") int contenance);

    @Query(name = "Salle.findByStatus")
    List<Salle> findByStatusNamed(@Param("statusSalle") SalleStatus status);

}
