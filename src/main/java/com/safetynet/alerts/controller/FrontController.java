package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.*;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.services.JsonReader;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.services.ListSearcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

@RestController
public class FrontController {


    @Autowired
    private JsonReader jsonReader;

    Logger logger = LoggerFactory.getLogger(JsonElts.class);

    public JsonElts sharedJson() throws IOException {
        return jsonReader.getJson();
    }

    public String urlLogger() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String url = request.getRequestURL().toString();
        String ipAddress = request.getRemoteAddr();
        return "The URL used to access this route is: " + url + " and the IP address is: " + ipAddress;
    }

    @GetMapping("/firestation")
    public FireStationCountDTO PeopleConcernedByStation(@RequestParam(value = "stationNumber") String stationNumber) throws IOException, ParseException {
        logger.info(urlLogger());
        ListSearcher listSearcher = new ListSearcher();
        List<FireStationDTO> allSelectedPersons = new ArrayList<>();
        List<FireStation> fireStationsByStation = listSearcher.searchAFireStationByValue("station", stationNumber, sharedJson().getFirestations());
        if (fireStationsByStation.isEmpty()) {
            logger.error("No firestation found for the value 'station':'".concat(stationNumber).concat("'"));
            return new FireStationCountDTO(new ArrayList<>(),-1,-1);
        }
        List<Person> allPersons = sharedJson().getPersons();
        int adultNbr = 0;
        int minorNbr = 0;

        for (FireStation a : fireStationsByStation) {
            String actualAddress = a.getAddress();
            for (Person b : allPersons) {
                String firstName = b.getFirstName();
                String lastName = b.getLastName();
                String fullName = firstName.concat(lastName).toLowerCase();
                MedicalRecord currentMedical = listSearcher.searchAMedicalRecordInAList(fullName, sharedJson().getMedicalrecords());

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

    @GetMapping("/childAlert")
    public ChildAlertCountDTO childAlert(@RequestParam(value = "address") String address) throws IOException, ParseException {
        logger.info(urlLogger());
        //Instanciate methods that allow to find all Person by address
        ListSearcher listSearcher = new ListSearcher();
        List<Person> listResidents = listSearcher.searchPersonsInAListByAddress(address, sharedJson().getPersons());

        //Return empty json if we have no one at this address
        if (listResidents.isEmpty()){
            logger.error("There is no residents for the address : "+address+" or the address doesn't exist");

            return new ChildAlertCountDTO(new ArrayList<>(),new ArrayList<>());
        }

        //Set an index to get who is minor or not
        int index = 0;
        List<Integer> minorsIndex = new ArrayList<>();
        List<ChildAlertDTO> minorsList = new ArrayList<>();
        List<ChildAlertDTO> adultList = new ArrayList<>();
        // Array that loop over residents searching for minors and adding them to the hashmap
        for (Person c : listResidents) {
            HashMap<String, String> minorsIndividual = new HashMap<>();

            String firstName = c.getFirstName();
            String lastName = c.getLastName();
            String fullName = firstName.concat(lastName).toLowerCase();
            MedicalRecord currentPersonMedicalRecord = listSearcher.searchAMedicalRecordInAList(fullName, sharedJson().getMedicalrecords());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy");
            LocalDate dob = LocalDate.parse(currentPersonMedicalRecord.getBirthdate(), formatter);
            Period period = Period.between(dob, LocalDate.now());
            if (period.getYears() <= 18) {
                ChildAlertDTO singleMinor = new ChildAlertDTO(c.getFirstName(),c.getLastName(),period.getYears());
                minorsList.add(singleMinor);
                minorsIndex.add(index);
            }
            index++;
        }

        //If there is no minors in the return empty json
        if(minorsList.isEmpty()){
            logger.error("There is not minor Person at this address");
            return new ChildAlertCountDTO(new ArrayList<>(),new ArrayList<>());
        }


        //Loop over all the residents again except we are going to skip the minors to only add adults to our hashmap
        int indexA = 0;

        for (Person c : listResidents) {
            if (!minorsIndex.contains(indexA)) {

                String firstName = c.getFirstName();
                String lastName = c.getLastName();
                String fullName = firstName.concat(lastName).toLowerCase();
                MedicalRecord currentPersonMedicalRecord = listSearcher.searchAMedicalRecordInAList(fullName, sharedJson().getMedicalrecords());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy");
                LocalDate dob = LocalDate.parse(currentPersonMedicalRecord.getBirthdate(), formatter);
                Period period = Period.between(dob, LocalDate.now());
                ChildAlertDTO singleAdult = new ChildAlertDTO(c.getFirstName(),c.getLastName(),period.getYears());
                adultList.add(singleAdult);
            }
            indexA++;
        }


        logger.debug("Returned data :"+ new ChildAlertCountDTO(minorsList,adultList));
        return new ChildAlertCountDTO(minorsList,adultList);
    }

    @GetMapping("/phoneAlert")
    public PhoneAlertDTO phonedAlert(@RequestParam(value = "firestation") String station) throws IOException, ParseException {
        logger.info(urlLogger());
        ListSearcher listSearcher = new ListSearcher();
        List<FireStation> firestationsConcerned = listSearcher.searchAFireStationByValue("station", station, sharedJson().getFirestations());
        List<String> firestationsAddresses = new ArrayList<>();

        for (FireStation a : firestationsConcerned) {
            firestationsAddresses.add(a.getAddress());
        }

        List<String> phonesList = new ArrayList<>();
        List<Person> allPersons = sharedJson().getPersons();
        for (Person a : allPersons) {
            if (firestationsAddresses.contains(a.getAddress())) {
                logger.debug("Adding people's phone number who match the given address");
                phonesList.add(a.getPhone());
            }
        }
        logger.debug("Returned data :"+new PhoneAlertDTO(phonesList));
        return new PhoneAlertDTO(phonesList);
    }

    @GetMapping("/fire")
    public FireAlertCountDTO fireAlert(@RequestParam(value = "address") String address) throws IOException, ParseException {
        logger.info(urlLogger());
        ListSearcher listSearcher = new ListSearcher();

        List<Person> selectedPersons = listSearcher.searchPersonsInAListByAddress(address, sharedJson().getPersons());
        FireStation selectedFirestation = listSearcher.searchAFireStationByAddress(address, sharedJson().getFirestations());

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
            MedicalRecord currentMedicalRecord = listSearcher.searchAMedicalRecordInAList(fullName, sharedJson().getMedicalrecords());
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

    @GetMapping("/flood/stations")
    public FloodAlertCountDTO floodAlert(@RequestParam(value = "stations") List<String> stations) throws IOException, ParseException {

        logger.info(urlLogger());
        HashMap<String,List<HashMap>> addressDisplayer = new HashMap<>();
        List<HashMap> allAddresses = new ArrayList<>();

        ListSearcher listSearcher = new ListSearcher();
        List<FireStation> firestationList = new ArrayList<>();

        for (String stationNbr : stations){
            List<FireStation> tmpFireStation = listSearcher.searchAFireStationByValue("station",stationNbr,sharedJson().getFirestations());
            for(FireStation a:tmpFireStation){
                firestationList.add(a);
            }
        }

        List<FloodAlertHousesDTO> listOfHouses = new ArrayList<>();
        List <FloodAlertPersonDTO> listOfPersons = new ArrayList<>();

        for (FireStation fireStation:firestationList){
            List<Person> selectedPersons = listSearcher.searchPersonsInAListByAddress(fireStation.getAddress(), sharedJson().getPersons());

            for (Person a : selectedPersons){

                //Concat name
                String firstName = a.getFirstName();
                String lastName = a.getLastName();
                String fullName = firstName.concat(lastName).toLowerCase();
                MedicalRecord currentMedicalRecord = listSearcher.searchAMedicalRecordInAList(fullName, sharedJson().getMedicalrecords());
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

    @GetMapping("/personInfo")
    public void personInfo(@RequestParam(value = "firstName") String firstName,@RequestParam (value = "lastName") String lastName) throws IOException, ParseException {

        List<Person> allPersons = sharedJson().getPersons();
        String lastNameParam = lastName;
        ListSearcher listSearcher = new ListSearcher();

        List<Person> selectedPersons = listSearcher.searchPersonsInAListByLastName(lastNameParam,allPersons);

        for(Person a : selectedPersons){

        }
    }

    @GetMapping("/communityEmail")
    public List<String> communityEmail(@RequestParam(value = "city") String city) throws IOException, ParseException {
    logger.info(urlLogger());
       List<String> allMails= new ArrayList<>();

       ListSearcher listSearcher = new ListSearcher();

       List<Person> personByCity = listSearcher.searchPersonsInAListByCity(city,sharedJson().getPersons());

       for (Person a : personByCity){
           allMails.add(a.getEmail());
       }
        logger.debug("Returned data "+allMails);
       return allMails;
    }

    }