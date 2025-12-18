package com.eadl.suivi_academique.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PersonnelDTO {

    private String codePersonnel; 
    
    private String nomPersonnel;

    private String loginPersonnel;

    private String passwordPersonnel;

    private String sexe;

    private String rolePersonnel;

}
