package com.eadl.suivi_academique.services.exceptions.salleexception;

public class SalleNotFoundException extends RuntimeException {
    public SalleNotFoundException(String message) {
        super(message);
    }
}