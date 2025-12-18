package com.eadl.suivi_academique.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.eadl.suivi_academique.dto.SalleDTO;
import com.eadl.suivi_academique.entities.Salle;


@Mapper(componentModel = "spring")

public interface SalleMapper {
    
    SalleDTO toDTO(Salle salle);
    
    @Mapping(target = "programmations", ignore = true)
    Salle toEntity(SalleDTO salleDTO);
    
}