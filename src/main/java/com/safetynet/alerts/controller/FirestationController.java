package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.FireAlertCountDTO;
import com.safetynet.alerts.dto.FireStationCountDTO;
import com.safetynet.alerts.dto.FloodAlertCountDTO;
import com.safetynet.alerts.services.FirestationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping
public class FirestationController {

    @Autowired
    private FirestationServices FirestationServices;

    @PostMapping("/firestation")
    public ResponseEntity<String> createFirestation (@RequestParam(value ="address") String address, @RequestParam(value ="station") String station) throws IOException {
        return FirestationServices.createFirestation(address,station);
    }


    @PutMapping("/firestation/{address}")
    public ResponseEntity<String> modifyStation (@PathVariable String address,@RequestParam(value="station") String station) throws IOException {
       return FirestationServices.modifyStation(address,station);
    }

    @DeleteMapping("/firestation")
    public ResponseEntity<String> deleteFirestations(@RequestParam(value="queryby") String param, @RequestParam(value="option") String option) throws IOException {
       return FirestationServices.deleteFirestations(param,option);
    }

    @GetMapping("/firestation")
    public FireStationCountDTO PeopleConcernedByStation(@RequestParam(value = "stationNumber") String stationNumber) throws IOException, ParseException {
        return FirestationServices.PeopleConcernedByStation(stationNumber);
    }

    @GetMapping("/fire")
    public FireAlertCountDTO fireAlert(@RequestParam(value = "address") String address) throws IOException, ParseException {
        return FirestationServices.fireAlert(address);
    }

    @GetMapping("/flood/stations")
    public FloodAlertCountDTO floodAlert(@RequestParam(value = "stations") List<String> stations) throws IOException, ParseException {
        return FirestationServices.floodAlert(stations);
    }


}
