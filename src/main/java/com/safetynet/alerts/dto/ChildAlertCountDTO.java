package com.safetynet.alerts.dto;

import java.util.List;

public class ChildAlertCountDTO {

    public ChildAlertCountDTO(List<ChildAlertDTO> minorsList, List<ChildAlertDTO> adultsInSameHouse) {
        this.minorsList = minorsList;
        this.adultsInSameHouse = adultsInSameHouse;
    }

    private final List<ChildAlertDTO> minorsList;

    private final List<ChildAlertDTO> adultsInSameHouse;

    public List<ChildAlertDTO> getMinorsList() {
        return minorsList;
    }

    public List<ChildAlertDTO> getAdultsInSameHouse() {
        return adultsInSameHouse;
    }

}
