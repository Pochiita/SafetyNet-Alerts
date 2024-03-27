package com.safetynet.alerts.dto;

import java.util.List;

public class FireAlertDTO {

    private final String lastName;

    private final int age;
    private final String phoneNumber;

    private final List<List<String>> MedialRecords;


    public FireAlertDTO(String lastName, int age, String phoneNumber, List<List<String>> medialRecords) {
        this.lastName = lastName;
        this.age = age;
        this.phoneNumber = phoneNumber;
        MedialRecords = medialRecords;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<List<String>> getMedialRecords() {
        return MedialRecords;
    }

    public int getAge() {
        return age;
    }
}
