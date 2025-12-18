package com.eadl.suivi_academique.services.interfaces;

import java.util.List;

import com.eadl.suivi_academique.dto.AffectationDTO;

public interface AffectationInterface {

    AffectationDTO create(AffectationDTO dto);

    List<AffectationDTO> getAll();

    AffectationDTO getById(String codeCours, String codePersonnel);

    void delete(String codeCours, String codePersonnel);

}
