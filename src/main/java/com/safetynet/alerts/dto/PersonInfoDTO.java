package com.safetynet.alerts.dto;

import java.util.List;

public class PersonInfoDTO {

    private final String name;
    private final int age;
    private final String mail;
    private final List<List<String>> MedicalRecords;

    public PersonInfoDTO(String name, int age, String mail, List<List<String>> medicalRecords) {
        this.name = name;
        this.age = age;
        this.mail = mail;
        MedicalRecords = medicalRecords;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getMail() {
        return mail;
    }

    public List<List<String>> getMedicalRecords() {
        return MedicalRecords;
    }
}
