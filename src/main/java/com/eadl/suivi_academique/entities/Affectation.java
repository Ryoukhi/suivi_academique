package com.eadl.suivi_academique.entities;


import jakarta.persistence.Basic;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Affectation {

    @EmbeddedId
    @Basic(optional = false)
    private AffectationId codeAffectation;

    @MapsId("codePersonnel")
    @ManyToOne
    @JoinColumn(name = "code_personnel", referencedColumnName = "codePersonnel")
    @Basic(optional = false)
    private Personnel personnel;

    @MapsId("codeCours")
    @ManyToOne
    @JoinColumn(name = "code_cours")
    @Basic(optional = false)
    private Cours cours;


}
