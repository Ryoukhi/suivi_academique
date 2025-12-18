package com.eadl.suivi_academique.entities;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.eadl.suivi_academique.utils.RolePersonnel;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Personnel implements UserDetails{

    @Id
    @Basic(optional = false)
    @Column(unique = true)
    private String codePersonnel; 
    
    @Basic(optional = false)
    private String nomPersonnel;

    @Basic(optional = false)
    private String loginPersonnel;

    @Basic(optional = false)
    private String passwordPersonnel;

    @Basic(optional = false)
    private String sexe;

    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private RolePersonnel rolePersonnel;

    @OneToMany(mappedBy = "personnelProg", cascade = CascadeType.ALL)
    private List<Programmation> programmations;

    @OneToMany(mappedBy = "personnelVal", cascade = CascadeType.ALL)
    private List<Programmation> validations;

    @OneToMany(mappedBy = "personnel")
    private List<Affectation> affectations;

    //Implementation de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority("ROLE_" + rolePersonnel.name()));

    }

    @Override
    public String getPassword(){

        return passwordPersonnel;

    }

    @Override
    public String getUsername() {
        return loginPersonnel;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
