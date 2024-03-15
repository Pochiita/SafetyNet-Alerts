package com.safetynet.alerts.dto;

import java.util.List;

public class FloodAlertHousesDTO {

    private String address;

    private List<FloodAlertPersonDTO> peronsInHouse;

    public FloodAlertHousesDTO(String address, List<FloodAlertPersonDTO> peronsInHouse) {
        this.address = address;
        this.peronsInHouse = peronsInHouse;
    }

    public String getAddress() {
        return address;
    }

    public List<FloodAlertPersonDTO> getPeronsInHouse() {
        return peronsInHouse;
    }
}
