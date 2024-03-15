package com.safetynet.alerts.dto;

import java.util.List;

public class PersonInfoCountDTO {

    private final List<PersonInfoDTO> persons;

    public PersonInfoCountDTO(List<PersonInfoDTO> persons) {
        this.persons = persons;
    }

    public List<PersonInfoDTO> getPersons() {
        return persons;
    }
}
