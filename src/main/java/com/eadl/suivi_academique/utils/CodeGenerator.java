package com.eadl.suivi_academique.utils;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import com.eadl.suivi_academique.repositories.PersonnelRepository;

@Service
public class CodeGenerator {

    private final PersonnelRepository personnelRepository;
    

    public CodeGenerator(PersonnelRepository personnelRepository) {
        this.personnelRepository = personnelRepository;
    }


    public String generate(String roleString){
        

        String prefix = switch (roleString){
            case "ENSEIGNANT" -> "ENS";
            case "RESPONSABLE_ACADEMIQUE" -> "RA";
            case "RESPONSABLE_DISCIPLINE" -> "RD";
            default -> null;
        };

        long randomNumber = ThreadLocalRandom.current().nextInt(10000, 99999);
        int year = LocalDate.now().getYear();
        String code = prefix + year + randomNumber;
        if (personnelRepository.existsById(code)) {

            return generate(roleString); // Regenerate if code already exists
        }
        else{
            return code;
        }

    }
}
