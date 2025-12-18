package com.eadl.suivi_academique.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.eadl.suivi_academique.dto.ProgrammationDTO;
import com.eadl.suivi_academique.entities.Programmation;

@Mapper(componentModel = "spring", uses = {SalleMapper.class, CoursMapper.class, PersonnelMapper.class})
public interface ProgrammationMapper {


    @Mapping(source = "salle", target = "salleDTO")
    @Mapping(source = "cours", target = "coursDTO")
    @Mapping(source = "personnelProg", target = "personnelProgDto")
    @Mapping(source = "personnelVal", target = "personnelValDto")
    ProgrammationDTO toDTO(Programmation programmation);

    @Mapping(source = "salleDTO", target = "salle")
    @Mapping(source = "coursDTO", target = "cours")
    @Mapping(source = "personnelProgDto", target = "personnelProg")
    @Mapping(source = "personnelValDto", target = "personnelVal")
    Programmation toEntity(ProgrammationDTO programmationDTO);
    
    List<ProgrammationDTO> tDtos(List<Programmation> programmation);

}
