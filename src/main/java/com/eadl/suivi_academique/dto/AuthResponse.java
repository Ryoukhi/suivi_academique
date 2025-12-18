package com.eadl.suivi_academique.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String codePersonnel;
    private String nomPersonnel;
    private String rolePersonnel;

}
