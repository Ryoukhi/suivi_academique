package com.eadl.suivi_academique.services.exceptions.affectationexception;

public class AffectationNotFoundException extends RuntimeException {
    public AffectationNotFoundException(String message) {
        super(message);
    }
}