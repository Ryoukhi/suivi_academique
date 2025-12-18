package com.eadl.suivi_academique.exceptions.affectationexception;

public class AffectationNotFoundException extends RuntimeException {
    public AffectationNotFoundException(String message) {
        super(message);
    }
}