package com.safetynet.alerts.services;

import com.safetynet.alerts.dto.*;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class FirestationServices {

    @Autowired
    private JsonReader allElements;

    ListSearcher listSearcher = new ListSearcher();
    Logger logger = LoggerFactory.getLogger(FirestationServices.class);

    public String urlLogger() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String url = request.getRequestURL().toString();
        String ipAddress = request.getRemoteAddr();
        return "The URL used to access this route is: " + url + " and the IP address is: " + ipAddress;
    }

    public ResponseEntity<String> createFirestation (String address, String station) throws IOException {
        logger.info(urlLogger());
        List<FireStation> fireStations = allElements.getJson().getFirestations();
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

    public ResponseEntity<String> modifyStation (@PathVariable String address, @RequestParam(value="station") String station) throws IOException {
        logger.info(urlLogger());
        List<FireStation> fireStations = allElements.getJson().getFirestations();
        FireStation selectedFirestation = listSearcher.searchAFireStationByAddress(address, fireStations);
        if (selectedFirestation != null) {
            selectedFirestation.setStation(station);
            String msg = " 'Firestation' correctly modified - New station number:".concat(selectedFirestation.getStation());
            logger.info(msg);
            return new ResponseEntity<>("Correctly modified", HttpStatus.OK);

        } else {
            String msg = " 'Firestation' couldn't be modified - Not found by value :".concat(address);
            logger.error(msg);
            return new ResponseEntity<>("Not modified", HttpStatus.BAD_REQUEST);
        }
    }


    public ResponseEntity<String> deleteFirestations(@RequestParam(value="queryby") String param, @RequestParam(value="option") String option) throws IOException {
        logger.info(urlLogger());

        List<FireStation> fireStations = allElements.getJson().getFirestations();
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

    public FireStationCountDTO PeopleConcernedByStation(@RequestParam(value = "stationNumber") String stationNumber) throws IOException, ParseException {
        logger.info(urlLogger());
        ListSearcher listSearcher = new ListSearcher();
        List<FireStationDTO> allSelectedPersons = new ArrayList<>();
        List<FireStation> fireStationsByStation = listSearcher.searchAFireStationByValue("station", stationNumber, allElements.getJson().getFirestations());
        if (fireStationsByStation.isEmpty()) {
            logger.error("No firestation found for the value 'station':'".concat(stationNumber).concat("'"));
            return new FireStationCountDTO(new ArrayList<>(),-1,-1);
        }
        List<Person> allPersons = allElements.getJson().getPersons();
        int adultNbr = 0;
        int minorNbr = 0;

        for (FireStation a : fireStationsByStation) {
            String actualAddress = a.getAddress();
            for (Person b : allPersons) {
                String firstName = b.getFirstName();
                String lastName = b.getLastName();
                String fullName = firstName.concat(lastName).toLowerCase();
                MedicalRecord currentMedical = listSearcher.searchAMedicalRecordInAList(fullName, allElements.getJson().getMedicalrecords());

                if (b.getAddress().equals(actualAddress) && currentMedical != null) {
                    FireStationDTO singlePersonDTO = new FireStationDTO(b.getFirstName(),b.getLastName(),b.getAddress(),b.getPhone());
                    allSelectedPersons.add(singlePersonDTO);
                    logger.debug("Setting all properties to return data");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy");
                    LocalDate dob = LocalDate.parse(currentMedical.getBirthdate(), formatter);
                    Period period = Period.between(dob, LocalDate.now());

                    if (period.getYears() > 18) {
                        adultNbr++;
                    } else {
                        minorNbr++;
                    }

                }
            }
        }

        FireStationCountDTO toReturnData = new FireStationCountDTO(allSelectedPersons,adultNbr,minorNbr);
        return toReturnData;
    }

    public FireAlertCountDTO fireAlert(@RequestParam(value = "address") String address) throws IOException, ParseException {
        logger.info(urlLogger());
        ListSearcher listSearcher = new ListSearcher();

        List<Person> selectedPersons = listSearcher.searchPersonsInAListByAddress(address, allElements.getJson().getPersons());
        FireStation selectedFirestation = listSearcher.searchAFireStationByAddress(address, allElements.getJson().getFirestations());

        if (selectedFirestation == null){
            logger.error("No firestation match this address : "+address);
            return new FireAlertCountDTO(new ArrayList<>(),"-1");
        }
        List<FireAlertDTO> listOfCitizens = new ArrayList<>();
        for (Person a : selectedPersons) {

            //Getting first and last name of someone to be able to get his MedicalRecord by concatenating both
            String firstName = a.getFirstName();
            String lastName = a.getLastName();
            String fullName = firstName.concat(lastName).toLowerCase();
            MedicalRecord currentMedicalRecord = listSearcher.searchAMedicalRecordInAList(fullName, allElements.getJson().getMedicalrecords());
            //Here we get the age by checking the difference between Today and birthdate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy");
            LocalDate dob = LocalDate.parse(currentMedicalRecord.getBirthdate(), formatter);
            Period period = Period.between(dob, LocalDate.now());

            List<List<String>> medicationsAllergies = new ArrayList<>();
            medicationsAllergies.add(currentMedicalRecord.getAllergies());
            medicationsAllergies.add(currentMedicalRecord.getMedications());
            FireAlertDTO individualPerson = new FireAlertDTO(currentMedicalRecord.getLastName(),period.getYears(), a.getPhone(),medicationsAllergies);
            //We add each individual to a list to be returned
            listOfCitizens.add(individualPerson);
        }

        FireAlertCountDTO returnedData = new FireAlertCountDTO(listOfCitizens,selectedFirestation.getStation());
        logger.debug("Returned data : "+returnedData);
        return returnedData;
    }

    public FloodAlertCountDTO floodAlert(@RequestParam(value = "stations") List<String> stations) throws IOException, ParseException {

        logger.info(urlLogger());
        HashMap<String,List<HashMap>> addressDisplayer = new HashMap<>();
        List<HashMap> allAddresses = new ArrayList<>();

        ListSearcher listSearcher = new ListSearcher();
        List<FireStation> firestationList = new ArrayList<>();

        for (String stationNbr : stations){
            List<FireStation> tmpFireStation = listSearcher.searchAFireStationByValue("station",stationNbr,allElements.getJson().getFirestations());
            for(FireStation a:tmpFireStation){
                firestationList.add(a);
            }
        }

        List<FloodAlertHousesDTO> listOfHouses = new ArrayList<>();
        List <FloodAlertPersonDTO> listOfPersons = new ArrayList<>();

        for (FireStation fireStation:firestationList){
            List<Person> selectedPersons = listSearcher.searchPersonsInAListByAddress(fireStation.getAddress(), allElements.getJson().getPersons());

            for (Person a : selectedPersons){

                //Concat name
                String firstName = a.getFirstName();
                String lastName = a.getLastName();
                String fullName = firstName.concat(lastName).toLowerCase();
                MedicalRecord currentMedicalRecord = listSearcher.searchAMedicalRecordInAList(fullName, allElements.getJson().getMedicalrecords());
                //Here we get the age by checking the difference between Today and birthdate
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy");
                LocalDate dob = LocalDate.parse(currentMedicalRecord.getBirthdate(), formatter);
                Period period = Period.between(dob, LocalDate.now());

                List<List<String>> medicationAllergies = new ArrayList<>();
                medicationAllergies.add(currentMedicalRecord.getMedications());
                medicationAllergies.add(currentMedicalRecord.getAllergies());
                FloodAlertPersonDTO individualPerson = new FloodAlertPersonDTO(a.getLastName(),a.getPhone(),period.getYears(),medicationAllergies);
                listOfPersons.add(individualPerson);

            }
            FloodAlertHousesDTO singleHouse = new FloodAlertHousesDTO(fireStation.getAddress(),listOfPersons);
            listOfHouses.add(singleHouse);
        }
        addressDisplayer.put("addresses",allAddresses);
        FloodAlertCountDTO returnedData = new FloodAlertCountDTO(listOfHouses);
        logger.debug("Returned data"+addressDisplayer);
        return returnedData;

    }
}
