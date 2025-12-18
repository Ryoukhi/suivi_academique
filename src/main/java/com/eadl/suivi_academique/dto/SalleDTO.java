package com.eadl.suivi_academique.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class SalleDTO {

    private String codeSalle; 

    private String descSalle;

    private int contenance;
    
    private String statusSalle; 

}
