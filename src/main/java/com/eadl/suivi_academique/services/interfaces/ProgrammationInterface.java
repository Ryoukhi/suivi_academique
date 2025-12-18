package com.eadl.suivi_academique.services.interfaces;


import java.util.List;
import com.eadl.suivi_academique.dto.ProgrammationDTO;
import com.eadl.suivi_academique.dto.ProgrammationRequest;

public interface ProgrammationInterface {

    // CREATE
	public ProgrammationDTO createProgrammation(ProgrammationRequest dto);

	// READ - all
	public List<ProgrammationDTO> getAllProgrammations();

	// READ - by id
	public ProgrammationDTO getProgrammationById(int codeProgrammation);

	// UPDATE
	public ProgrammationDTO updateProgrammation(int codeProgrammation, ProgrammationRequest dto);

	// DELETE - by id
	public void deleteProgrammation(int codeProgrammation);
	
	// DELETE - all
	public void deleteAllProgrammations();


}
