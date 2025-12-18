package com.eadl.suivi_academique.services.exceptions.salleexception;

public class SalleIndisponibleException extends RuntimeException {
    public SalleIndisponibleException(String message) {
        super(message);
    }
}