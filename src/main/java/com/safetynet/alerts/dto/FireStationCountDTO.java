package com.safetynet.alerts.dto;

import java.util.List;

public class FireStationCountDTO {

    private final List<FireStationDTO> personsStation;
    private final int totalAdultsNumber;

    public List<FireStationDTO> getPersonsStation() {
        return personsStation;
    }

    public int getTotalAdultsNumber() {
        return totalAdultsNumber;
    }

    public int getTotalChildrenNumber() {
        return totalChildrenNumber;
    }

    private final int totalChildrenNumber;

    public FireStationCountDTO(List<FireStationDTO> personsStation, int totalAdultsNumber, int totalChildrenNumber) {
        this.personsStation = personsStation;
        this.totalAdultsNumber = totalAdultsNumber;
        this.totalChildrenNumber = totalChildrenNumber;
    }
}
