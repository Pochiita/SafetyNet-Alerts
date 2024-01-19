package com.safetynet.alerts.model;

import java.util.HashMap;

public class FireStations {

    private String address;
    private int stationNbr;

    private HashMap<String, String> station ;


    public int getStationNbr() {
        return stationNbr;
    }
    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address = address;
    }

    public void setStationNbr(int stationNbr) {
        this.stationNbr = stationNbr;
    }

    public void setStation(HashMap<String, String> station) {
        this.station = station;
    }

    public HashMap<String, String> getStation() {
        return station;
    }
}
