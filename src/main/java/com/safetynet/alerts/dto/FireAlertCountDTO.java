package com.safetynet.alerts.dto;

import java.util.List;

public class FireAlertCountDTO {

    private final List<FireAlertDTO> citizens;

    private final String nbrOfFireStation;

    public FireAlertCountDTO(List<FireAlertDTO> citizens, String nbrOfFireStation) {
        this.citizens = citizens;
        this.nbrOfFireStation = nbrOfFireStation;
    }

    public List<FireAlertDTO> getCitizens() {
        return citizens;
    }

    public String getNbrOfFireStation() {
        return nbrOfFireStation;
    }
}
