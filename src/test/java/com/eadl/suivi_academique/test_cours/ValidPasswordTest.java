package com.eadl.suivi_academique.test_cours;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.eadl.suivi_academique.utils.PasswordValidator;

public class ValidPasswordTest {

    static String password = "1234";

    @BeforeEach
    void beforeTestValid() {
        System.out.println("Début des tests de validation de mot de passe");
        password = password + "567";
    }

    @AfterEach
    void afterTestValid() {
        System.out.println("Fin des tests de validation de mot de passe" + password);
    }


    @Test
    public void testPassword() {

        PasswordValidator PasswordValidator = new PasswordValidator();

        assertEquals(true,PasswordValidator.isValid(password), "Verifier longueur mot de passe ");
        
    }

}
