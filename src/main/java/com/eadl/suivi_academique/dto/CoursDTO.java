package com.eadl.suivi_academique.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CoursDTO {

    private String codeCours;

    private String labelCours;

    private String descCours;

    private int nbCreditCours;

    private int nbHeureCours;

}
