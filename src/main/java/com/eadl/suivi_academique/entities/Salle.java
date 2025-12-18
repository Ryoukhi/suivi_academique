package com.eadl.suivi_academique.entities;

import java.util.List;

import com.eadl.suivi_academique.utils.SalleStatus;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NamedQueries({
    @NamedQuery(name = "Salle.findAll", query = "SELECT s FROM Salle s"),
    @NamedQuery(name = "Salle.findByCodeSalle", query = "SELECT s FROM Salle s WHERE s.codeSalle = :codeSalle"),
    @NamedQuery(name = "Salle.findByContenanceGreaterThanEquals", query = "SELECT s FROM Salle s WHERE s.contenance >= :contenance"),
    @NamedQuery(name = "Salle.findByStatus", query = "SELECT s FROM Salle s WHERE s.statusSalle = :statusSalle")
})
@Entity
@Table(name = "salles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Salle {

    @Id
    @Basic(optional = false)
    private String codeSalle;

    @Basic(optional = true)
    private String descSalle;

    @Basic(optional = false)
    private int contenance;

    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private SalleStatus statusSalle;

    @OneToMany(mappedBy = "salle", cascade = CascadeType.ALL)
    private List<Programmation> programmations;

}
