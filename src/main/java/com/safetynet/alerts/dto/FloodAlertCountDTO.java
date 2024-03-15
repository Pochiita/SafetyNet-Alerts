package com.safetynet.alerts.dto;

import java.util.List;

public class FloodAlertCountDTO {

    private final List<FloodAlertHousesDTO> homes;

    public FloodAlertCountDTO(List<FloodAlertHousesDTO> homes) {
        this.homes = homes;
    }

    public List<FloodAlertHousesDTO> getHomes() {
        return homes;
    }
}
