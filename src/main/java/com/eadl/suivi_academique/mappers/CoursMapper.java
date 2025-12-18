package com.eadl.suivi_academique.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.eadl.suivi_academique.dto.CoursDTO;
import com.eadl.suivi_academique.entities.Cours;


@Mapper(componentModel = "spring", uses = {ProgrammationMapper.class, AffectationMapper.class})
public interface CoursMapper {

    CoursDTO toDTO(Cours cours);

    @Mapping(target = "programmations", ignore = true)
    @Mapping(target = "affectations", ignore = true)
    Cours toEntity(CoursDTO coursDTO);
    
    List<CoursDTO> toDTOs(List<Cours> coursList);

    List<Cours> toEntities(List<CoursDTO> coursDTOList);

}
