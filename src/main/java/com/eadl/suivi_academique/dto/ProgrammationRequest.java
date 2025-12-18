package com.eadl.suivi_academique.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProgrammationRequest {

    private int codeProgrammation;

    private int nbHeureProgammation;

    private Date dateProgammation;

    private Date debutProgammation;

    private Date finProgammation;

    private String statusProgrammation;

    // relations = seulement leurs IDs
    private String codeSalle;

    private String codeCours;

    private String codePersonnelProg;
    
    private String codePersonnelVal;
}
