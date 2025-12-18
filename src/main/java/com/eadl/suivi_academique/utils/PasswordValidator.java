package com.eadl.suivi_academique.utils;

public class PasswordValidator {

    public boolean isValid(String password) {
        // Example validation: password must be at least 6 characters long
        return password != null && password.length() >= 6;
    }

}
