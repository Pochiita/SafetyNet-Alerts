package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.FireAlertCountDTO;
import com.safetynet.alerts.dto.FireStationCountDTO;
import com.safetynet.alerts.dto.FireStationDTO;
import com.safetynet.alerts.dto.FloodAlertCountDTO;
import com.safetynet.alerts.services.FirestationServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private FirestationServices firestationServices;

    Logger logger = LoggerFactory.getLogger(FirestationController.class);
    @PostMapping("/firestation")
    public ResponseEntity<String> createFirestation(@RequestParam(value = "address") String address, @RequestParam(value = "station") String station) throws IOException {
        ResponseEntity<String> response = firestationServices.createFirestation(address, station);

        if (response.getStatusCode().is2xxSuccessful()){
            String msg = " 'Firestation' correctly created";
            logger.info(msg);
        }else{
            String msg = " 'Firestation' couldn't be created";
            logger.error(msg);
        }

        return response;
    }


    @PutMapping("/firestation/{address}")
    public ResponseEntity<String> modifyStation(@PathVariable String address, @RequestParam(value = "station") String station) throws IOException {
        ResponseEntity<String> response = firestationServices.modifyStation(address, station);

        if (response.getStatusCode().is2xxSuccessful()){
            String msg = " 'Firestation' correctly modified";
            logger.info(msg);
        }else{
            String msg = " 'Firestation' couldn't be modified - Not found by value :".concat(address);
            logger.error(msg);
        }

        return response;
    }

    @DeleteMapping("/firestation")
    public ResponseEntity<String> deleteFirestations(@RequestParam(value = "queryby") String param, @RequestParam(value = "option") String option) throws IOException {
        ResponseEntity<String> response = firestationServices.deleteFirestations(param, option);

        if (response.getStatusCode().is2xxSuccessful()){
            logger.info("Firestation deleted");
        }else{
            logger.error("No firestation selected");
        }

        return response;
    }

    @GetMapping("/firestation")
    public FireStationCountDTO PeopleConcernedByStation(@RequestParam(value = "stationNumber") String stationNumber) throws IOException, ParseException {
        FireStationCountDTO response = firestationServices.PeopleConcernedByStation(stationNumber);
        logger.debug("Returned data :" + response);
        return response;
    }

    @GetMapping("/fire")
    public FireAlertCountDTO fireAlert(@RequestParam(value = "address") String address) throws IOException, ParseException {
        FireAlertCountDTO response =  firestationServices.fireAlert(address);
        logger.debug("Returned data :" + response);
        return response;
    }

    @GetMapping("/flood/stations")
    public FloodAlertCountDTO floodAlert(@RequestParam(value = "stations") List<String> stations) throws IOException, ParseException {
        FloodAlertCountDTO response = firestationServices.floodAlert(stations);
        logger.debug("Returned data :" + response);
        return response;
    }


}
