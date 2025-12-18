package com.eadl.suivi_academique.exceptions.programmationexception;

public class ProgrammationNotFoundException extends RuntimeException {
    public ProgrammationNotFoundException(String message) {
        super(message);
    }
}