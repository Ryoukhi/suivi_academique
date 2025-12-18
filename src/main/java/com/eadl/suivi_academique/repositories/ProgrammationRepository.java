package com.eadl.suivi_academique.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.eadl.suivi_academique.entities.Programmation;
import com.eadl.suivi_academique.utils.StatusProgrammation;


public interface ProgrammationRepository extends JpaRepository<Programmation, Integer>{

	@Query(name = "Programmation.findAll")
	List<Programmation> findAllByNamed();

	@Query(name = "Programmation.findByCode")
	Optional<Programmation> findByCodeNamed(@Param("codeProgrammation") int codeProgrammation);

	@Query(name = "Programmation.findByStatus")
	List<Programmation> findByStatusNamed(@Param("statusProgrammation") StatusProgrammation status);

	@Query(name = "Programmation.findBySalle")
	List<Programmation> findBySalleCode(@Param("codeSalle") String codeSalle);

	@Query(name = "Programmation.findByCours")
	List<Programmation> findByCoursCode(@Param("codeCours") String codeCours);

	@Query(name = "Programmation.findByPersonnelProg")
	List<Programmation> findByPersonnelProgCode(@Param("codePersonnel") String codePersonnel);

	@Query(name = "Programmation.findByPersonnelVal")
	List<Programmation> findByPersonnelValCode(@Param("codePersonnel") String codePersonnel);

}
