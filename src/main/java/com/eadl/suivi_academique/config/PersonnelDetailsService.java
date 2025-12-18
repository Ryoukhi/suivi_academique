package com.eadl.suivi_academique.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eadl.suivi_academique.repositories.PersonnelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonnelDetailsService implements UserDetailsService {

    private final PersonnelRepository personnelRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        return personnelRepository.findByLoginPersonnel(username).orElseThrow(() -> new UsernameNotFoundException("Personnel introuvable avec le login: " + username));
    }


}
