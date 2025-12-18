package com.eadl.suivi_academique.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import com.eadl.suivi_academique.dto.PersonnelDTO;
import com.eadl.suivi_academique.entities.Personnel;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonnelMapper {


    PersonnelDTO toDTO(Personnel personnel);
    Personnel toEntity(PersonnelDTO personnelDTO);

}
