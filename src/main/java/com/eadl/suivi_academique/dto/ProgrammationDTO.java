package com.eadl.suivi_academique.dto;

import java.sql.Date;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ProgrammationDTO {

    private int codeProgrammation;

    private int nbHeureProgammation;

    private Date dateProgammation;

    private Date debutProgammation;

    private Date finProgammation;

    private String statusProgrammation;

    private SalleDTO salleDTO;

    private CoursDTO coursDTO;

    private PersonnelDTO personnelProgDto;
    
    private PersonnelDTO personnelValDto;



}
