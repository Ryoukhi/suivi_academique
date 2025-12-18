package com.eadl.suivi_academique.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.eadl.suivi_academique.dto.AffectationDTO;
import com.eadl.suivi_academique.entities.Affectation;

@Mapper(componentModel = "spring", uses = {PersonnelMapper.class, CoursMapper.class})
public interface AffectationMapper {

    
    @Mapping(source = "codeAffectation.codePersonnel", target = "codePersonnel")
    @Mapping(source = "codeAffectation.codeCours", target = "codeCours")
    @Mapping(source = "personnel", target = "personnelDTO")
    @Mapping(source = "cours", target = "coursDTO")
    AffectationDTO toDTO(Affectation affectation);

    @Mapping(source = "codePersonnel", target = "codeAffectation.codePersonnel")
    @Mapping(source = "codeCours", target = "codeAffectation.codeCours")
    @Mapping(source = "personnelDTO", target = "personnel")
    @Mapping(source = "coursDTO", target = "cours")
    Affectation toEntity(AffectationDTO affectationDTO);
    
    List<AffectationDTO> toDtos(List<Affectation> affectations);
}
