package com.eadl.suivi_academique.services.exceptions.personnelexception;

public class PersonnelNotFoundException extends RuntimeException {
    public PersonnelNotFoundException(String message) {
        super(message);
    }
}
