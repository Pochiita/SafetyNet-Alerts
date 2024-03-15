package com.safetynet.alerts.dto;

import java.util.List;

public class PhoneAlertDTO {

    private final List<String> phoneNumber;

    public PhoneAlertDTO(List<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getPhoneNumber() {
        return phoneNumber;
    }
}
