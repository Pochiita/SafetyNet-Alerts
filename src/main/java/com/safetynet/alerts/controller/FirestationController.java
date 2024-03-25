package com.safetynet.alerts.controller;

import com.safetynet.alerts.services.FirestationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping
public class FirestationController {

    @Autowired
    private FirestationServices FirestationServices;

    @PostMapping("/firestation/")
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
}
