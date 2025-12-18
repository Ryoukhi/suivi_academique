package com.eadl.suivi_academique.services.interfaces;

import com.eadl.suivi_academique.dto.AuthRequest;
import com.eadl.suivi_academique.dto.AuthResponse;
import com.eadl.suivi_academique.dto.PersonnelDTO;

public interface AuthentificationInterface {

    public AuthResponse authenticate(AuthRequest request);

    public AuthResponse register(PersonnelDTO personnel);
}
