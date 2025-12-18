package com.eadl.suivi_academique.entities;

import java.io.Serializable;

import jakarta.persistence.Basic;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor

public class AffectationId implements Serializable {

    @Basic(optional = false)
    private String codeCours;

    @Basic(optional = false)
    private String codePersonnel;

    public AffectationId(String codeCours, String codePersonnel) {
        this.codeCours = codeCours;
        this.codePersonnel = codePersonnel;
    }


}
