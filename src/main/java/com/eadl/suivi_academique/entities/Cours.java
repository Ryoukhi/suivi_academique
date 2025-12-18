package com.eadl.suivi_academique.entities;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cours")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Cours {

    @Id
    @Basic(optional = false)
    @Column(unique = true)
    @Length(min = 5)
    private String codeCours;

    @Basic(optional = false)
    private String labelCours;

    @Basic(optional = false)
    private String descCours;

    @Basic(optional = false)
    private int nbCreditCours;

    @Basic(optional = false)
    private int nbHeureCours;

    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL)
    private List<Programmation> programmations;

    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL)
    private List<Affectation> affectations;


}
