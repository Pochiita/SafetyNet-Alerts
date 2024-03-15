package com.safetynet.alerts.dto;

import java.util.List;

public class FloodAlertPersonDTO {

    private final String lastName;

    private final String phoneNumber;

    private final int age;

    private final List<List<String>> MedialRecords;

    public FloodAlertPersonDTO(String lastName, String phoneNumber, int age, List<List<String>> medialRecords) {
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.age = age;
        MedialRecords = medialRecords;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getAge() {
        return age;
    }

    public List<List<String>> getMedialRecords() {
        return MedialRecords;
    }
}
