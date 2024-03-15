package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.FireStation;
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
    private JsonReader jsonReader;

    Logger logger = LoggerFactory.getLogger(JsonElts.class);

    ListSearcher listSearcher = new ListSearcher();

    public JsonElts sharedJson() throws IOException {
        return jsonReader.getJson();
    }

    public String urlLogger() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String url = request.getRequestURL().toString();
        String ipAddress = request.getRemoteAddr();
        return "The URL used to access this route is: " + url + " and the IP address is: " + ipAddress;
    }

    @PostMapping("/firestation/")
    public ResponseEntity<String> createFirestation (@RequestParam(value ="address") String address, @RequestParam(value ="station") String station) throws IOException {
        logger.info(urlLogger());
        List<FireStation> fireStations = sharedJson().getFirestations();
        int listSize = fireStations.size();
        FireStation newFirestation = new FireStation();
        newFirestation.setAddress(address);
        newFirestation.setStation(station);
        fireStations.add(newFirestation);
        if (fireStations.size()>listSize){
            String msg = " 'Firestation' correctly created - 'FireStation' :".concat(newFirestation.getStation()).concat(" - ").concat(newFirestation.getAddress());
            logger.info(msg);
        }else{
            String msg = " 'Firestation' couldn't be created";
            logger.error(msg);
            return new ResponseEntity<>("Not created", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Correctly created", HttpStatus.OK);
    }


    @PutMapping("/firestation/{address}")
    public ResponseEntity<String> modifyStation (@PathVariable String address,@RequestParam(value="station") String station) throws IOException {
        logger.info(urlLogger());
        List<FireStation> fireStations = sharedJson().getFirestations();
        FireStation selectedFirestation = listSearcher.searchAFireStationByAddress(address,fireStations);
        if (selectedFirestation != null) {
            selectedFirestation.setStation(station);
            String msg = " 'Firestation' correctly modified - New station number:".concat(selectedFirestation.getStation());
            logger.info(msg);
            return new ResponseEntity<>("Correctly modified", HttpStatus.OK);

        }else{
            String msg = " 'Firestation' couldn't be modified - Not found by value :".concat(address);
            logger.error(msg);
            return new ResponseEntity<>("Not modified", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/firestation")
    public ResponseEntity<String> deleteFirestations(@RequestParam(value="queryby") String param, @RequestParam(value="option") String option) throws IOException {
        logger.info(urlLogger());

        List<FireStation> fireStations = sharedJson().getFirestations();
        int index = 0;
        boolean canDelete = false;
        logger.debug("Checking 'queryby' url parameter value");
        if (!Objects.equals(param, "address") && !Objects.equals(param, "station")){
            logger.error("Incorrect value 'queryby' must be either 'address' or 'station' ");
            return new ResponseEntity<>("Incorrect value 'queryby' must be either 'address' or 'station' ", HttpStatus.BAD_REQUEST);
        }
        logger.debug("Checking that the 'station' url parameter contains only digits");
        if (Objects.equals(param, "station")){
            if (!option.matches("\\d+")){
                logger.error("Incorrect value 'option' must be digits-only'");
                return new ResponseEntity<>("Incorrect value 'option' must be digits-only' ", HttpStatus.BAD_REQUEST);
            }
        }
        List<FireStation> selectedFireStations = listSearcher.searchAFireStationByValue(param,option,fireStations);
        if (selectedFireStations.size() >0){
            for(FireStation firestation : selectedFireStations){
                FireStation currentFirestation = firestation;
                String logMsg = "Station with address : ".concat(currentFirestation.getAddress()).concat("and station number :").concat(currentFirestation.getStation()).concat(" Deleted");
                fireStations.remove(firestation);
                logger.info(logMsg);
            }
        }else{
            logger.error("No firestation selected");

            return new ResponseEntity<>("No Firestation selected",HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Correctly deleted",HttpStatus.OK);
    }
}
