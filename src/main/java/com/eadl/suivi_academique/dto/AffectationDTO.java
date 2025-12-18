package com.eadl.suivi_academique.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class AffectationDTO {

    private String codePersonnel;
    private String codeCours;
    private PersonnelDTO personnelDTO;
    private CoursDTO coursDTO;
}



