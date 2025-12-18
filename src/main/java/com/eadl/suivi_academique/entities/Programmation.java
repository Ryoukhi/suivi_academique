package com.eadl.suivi_academique.entities;

import java.util.Date;

import com.eadl.suivi_academique.utils.StatusProgrammation;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.*;

@NamedQueries({
    @NamedQuery(name = "Programmation.findAll", query = "SELECT p FROM Programmation p"),
    @NamedQuery(name = "Programmation.findByCode", query = "SELECT p FROM Programmation p WHERE p.codeProgrammation = :codeProgrammation"),
    @NamedQuery(name = "Programmation.findByStatus", query = "SELECT p FROM Programmation p WHERE p.statusProgrammation = :statusProgrammation"),
    @NamedQuery(name = "Programmation.findBySalle", query = "SELECT p FROM Programmation p WHERE p.salle.codeSalle = :codeSalle"),
    @NamedQuery(name = "Programmation.findByCours", query = "SELECT p FROM Programmation p WHERE p.cours.codeCours = :codeCours"),
    @NamedQuery(name = "Programmation.findByPersonnelProg", query = "SELECT p FROM Programmation p WHERE p.personnelProg.codePersonnel = :codePersonnel"),
    @NamedQuery(name = "Programmation.findByPersonnelVal", query = "SELECT p FROM Programmation p WHERE p.personnelVal.codePersonnel = :codePersonnel")
})
@Entity
@Table(name = "programmation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Programmation {

    @Id
    @Column(unique = true)
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codeProgrammation;

    @Basic(optional = false)
    private int nbHeureProgammation;

    @Basic(optional = false)
    private Date dateProgammation;

    @Basic(optional = false)
    private Date debutProgammation;

    @Basic(optional = false)
    private Date finProgammation;

    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private StatusProgrammation statusProgrammation;

    @ManyToOne
    @JoinColumn(name = "code_salle", referencedColumnName = "codeSalle")
    private Salle salle;

    @ManyToOne
    @JoinColumn(name = "code_cours", referencedColumnName = "codeCours")
    private Cours cours;

    @ManyToOne
    @JoinColumn(name = "code_personnel_prog", referencedColumnName = "codePersonnel")
    private Personnel personnelProg;

    @ManyToOne
    @JoinColumn(name = "code_personnel_val", referencedColumnName = "codePersonnel")
    private Personnel personnelVal;

}
