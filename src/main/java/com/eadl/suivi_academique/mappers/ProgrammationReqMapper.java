package com.eadl.suivi_academique.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.eadl.suivi_academique.dto.ProgrammationRequest;
import com.eadl.suivi_academique.entities.Programmation;

@Mapper(componentModel = "spring")
public interface ProgrammationReqMapper {

    @Mapping(source = "salle.codeSalle", target = "codeSalle")
    @Mapping(source = "cours.codeCours", target = "codeCours")
    @Mapping(source = "personnelProg.codePersonnel", target = "codePersonnelProg")
    @Mapping(source = "personnelVal.codePersonnel", target = "codePersonnelVal")
    ProgrammationRequest toDTO(Programmation programmation);

    @Mapping(source = "codeSalle", target = "salle.codeSalle")
    @Mapping(source = "codeCours", target = "cours.codeCours")
    @Mapping(source = "codePersonnelProg", target = "personnelProg.codePersonnel")
    @Mapping(source = "codePersonnelVal", target = "personnelVal.codePersonnel")
    Programmation toEntity(ProgrammationRequest programmationRequest);

}
