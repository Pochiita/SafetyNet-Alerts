package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.services.FirestationServices;
import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.services.JsonReader;
import com.safetynet.alerts.services.ListSearcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping
public class FireStationController {

    @Autowired
    private FirestationServices firestationServices;

    @PostMapping("/firestation/")
    public ResponseEntity<String> createFirestation (@RequestParam(value ="address") String address, @RequestParam(value ="station") String station) throws IOException {
        return firestationServices.createFirestation(address,station);
    }


    @PutMapping("/firestation/{address}")
    public ResponseEntity<String> modifyStation (@PathVariable String address,@RequestParam(value="station") String station) throws IOException {
       return firestationServices.modifyStation(address,station);
    }

    @DeleteMapping("/firestation")
    public ResponseEntity<String> deleteFirestations(@RequestParam(value="queryby") String param, @RequestParam(value="option") String option) throws IOException {
       return firestationServices.deleteFirestations(param,option);
    }
}
