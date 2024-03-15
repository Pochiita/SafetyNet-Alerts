package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.FireStationCountDTO;
import com.safetynet.alerts.dto.FireStationDTO;
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
            return new FireStationCountDTO(new ArrayList<>(),0,0);
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
    public List<HashMap> childAlert(@RequestParam(value = "address") String address) throws IOException, ParseException {
        logger.info(urlLogger());
        //Setup variables that displays the json correctly
        List<HashMap> returningData = new ArrayList<>();
        HashMap<String, List<HashMap>> minorsList = new HashMap<>();
        HashMap<String, List<HashMap>> otherResidents = new HashMap<>();
        List<HashMap> allMinors = new ArrayList<>();
        List<HashMap> allAdults = new ArrayList<>();

        //Instanciate methods that allow to find all Person by address
        ListSearcher listSearcher = new ListSearcher();
        List<Person> listResidents = listSearcher.searchPersonsInAListByAddress(address, sharedJson().getPersons());

        //Return empty json if we have no one at this address
        if (listResidents.isEmpty()){
            logger.error("There is no residents for the address : "+address+" or the address doesn't exist");

            return returningData;
        }

        //Set an index to get who is minor or not
        int index = 0;
        List<Integer> minorsIndex = new ArrayList<>();

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
                minorsIndividual.put("firstName", c.getFirstName());
                minorsIndividual.put("lastName", c.getLastName());
                minorsIndividual.put("age", String.valueOf(period.getYears()));
                allMinors.add(minorsIndividual);
                minorsIndex.add(index);
            }
            index++;
        }

        //If there is no minors in the return empty json
        if(allMinors.isEmpty()){
            logger.error("There is not minor Person at this address");
            return returningData;
        }


        //Loop over all the residents again except we are going to skip the minors to only add adults to our hashmap
        int indexA = 0;

        for (Person c : listResidents) {
            HashMap<String, String> adultIndividual = new HashMap<>();
            if (!minorsIndex.contains(indexA)) {
                String firstName = c.getFirstName();
                String lastName = c.getLastName();
                String fullName = firstName.concat(lastName).toLowerCase();
                MedicalRecord currentPersonMedicalRecord = listSearcher.searchAMedicalRecordInAList(fullName, sharedJson().getMedicalrecords());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy");
                LocalDate dob = LocalDate.parse(currentPersonMedicalRecord.getBirthdate(), formatter);
                Period period = Period.between(dob, LocalDate.now());
                adultIndividual.put("firstName", c.getFirstName());
                adultIndividual.put("lastName", c.getLastName());
                adultIndividual.put("age", String.valueOf(period.getYears()));
                allAdults.add(adultIndividual);
                minorsIndex.add(index);
            }
            indexA++;
        }
        otherResidents.put("otherResidents",allAdults);
        minorsList.put("minor",allMinors);
        returningData.add(minorsList);
        returningData.add(otherResidents);
        logger.debug("Returned data :"+returningData);
        return returningData;
    }

    @GetMapping("/phoneAlert")
    public List<String> phonedAlert(@RequestParam(value = "firestation") String station) throws IOException, ParseException {
        logger.info(urlLogger());
        List<String> phoneNumbers = new ArrayList<>();
        ListSearcher listSearcher = new ListSearcher();

        List<FireStation> firestationsConcerned = listSearcher.searchAFireStationByValue("station", station, sharedJson().getFirestations());
        List<String> firestationsAddresses = new ArrayList<>();

        for (FireStation a : firestationsConcerned) {
            firestationsAddresses.add(a.getAddress());
        }

        List<Person> allPersons = sharedJson().getPersons();
        for (Person a : allPersons) {
            if (firestationsAddresses.contains(a.getAddress())) {
                logger.debug("Adding people's phone number who match the given address");
                phoneNumbers.add(a.getPhone());
            }
        }
        logger.debug("Returned data :"+phoneNumbers);
        return phoneNumbers;
    }

    @GetMapping("/fire")
    public List<HashMap> fireAlert(@RequestParam(value = "address") String address) throws IOException, ParseException {
        logger.info(urlLogger());

        List<HashMap> returningData = new ArrayList<>();
        HashMap<String, List<HashMap>> peopleDisplayer = new HashMap<>();
        List<HashMap> peopleList = new ArrayList<>();
        HashMap<String,String> firestationDisplayer = new HashMap<>();

        ListSearcher listSearcher = new ListSearcher();

        List<Person> selectedPersons = listSearcher.searchPersonsInAListByAddress(address, sharedJson().getPersons());
        FireStation selectedFirestation = listSearcher.searchAFireStationByAddress(address, sharedJson().getFirestations());

        if (selectedFirestation == null){
            logger.error("No firestation match this address : "+address);
            return returningData;
        }

        for (Person a : selectedPersons) {
            //Setting variables that will be used to return the json and that represent a single person
            HashMap<String, HashMap> individualPerson = new HashMap<>();
            HashMap<String,String> individualTraits = new HashMap<>();
            HashMap<String,List<String>> individualArray = new HashMap<>();
            //Getting first and last name of someone to be able to get his MedicalRecord by concatenating both
            String firstName = a.getFirstName();
            String lastName = a.getLastName();
            String fullName = firstName.concat(lastName).toLowerCase();
            MedicalRecord currentMedicalRecord = listSearcher.searchAMedicalRecordInAList(fullName, sharedJson().getMedicalrecords());
            //Here we get the age by checking the difference between Today and birthdate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy");
            LocalDate dob = LocalDate.parse(currentMedicalRecord.getBirthdate(), formatter);
            Period period = Period.between(dob, LocalDate.now());
            //We set every attributes needed for the json
            individualTraits.put("lastName", currentMedicalRecord.getLastName());
            individualTraits.put("phone", a.getPhone());
            individualTraits.put("age", String.valueOf(period.getYears()));
            individualArray.put("medication", currentMedicalRecord.getMedications());
            individualArray.put("allergies", currentMedicalRecord.getAllergies());
            individualPerson.put("medical",individualArray);
            individualPerson.put("personnal",individualTraits);
            //We add each individual to a list to be returned
            peopleList.add(individualPerson);

        }

        firestationDisplayer.put("firesatationNbr",selectedFirestation.getStation());
        peopleDisplayer.put("persons",peopleList);

        returningData.add(peopleDisplayer);
        returningData.add(firestationDisplayer);
        logger.debug("Returned data : "+returningData);
        return returningData;
    }

    @GetMapping("/flood/stations")
    public HashMap<String,List<HashMap>> floodAlert(@RequestParam(value = "stations") List<String> stations) throws IOException, ParseException {

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

        for (FireStation fireStation:firestationList){
            List<Person> selectedPersons = listSearcher.searchPersonsInAListByAddress(fireStation.getAddress(), sharedJson().getPersons());
            HashMap<String,List<HashMap>> individualAddress = new HashMap<>();
            List<HashMap> allPersons = new ArrayList<>();
            for (Person a : selectedPersons){
                HashMap<String,HashMap> individualPerson = new HashMap<>();
                HashMap<String,String> individualTraits = new HashMap<>();
                HashMap<String,List<String>> individualArray = new HashMap<>();
                individualTraits.put("lastName",a.getLastName());
                individualTraits.put("phone",a.getPhone());
                //Concat name
                String firstName = a.getFirstName();
                String lastName = a.getLastName();
                String fullName = firstName.concat(lastName).toLowerCase();
                MedicalRecord currentMedicalRecord = listSearcher.searchAMedicalRecordInAList(fullName, sharedJson().getMedicalrecords());
                //Here we get the age by checking the difference between Today and birthdate
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy");
                LocalDate dob = LocalDate.parse(currentMedicalRecord.getBirthdate(), formatter);
                Period period = Period.between(dob, LocalDate.now());
                //Set Age
                individualTraits.put("age", String.valueOf(period.getYears()));

                individualArray.put("medications",currentMedicalRecord.getMedications());
                individualArray.put("allergies",currentMedicalRecord.getAllergies());
                individualPerson.put("personnal",individualTraits);
                individualPerson.put("medical",individualArray);
                allPersons.add(individualPerson);

            }
            individualAddress.put(fireStation.getAddress(),allPersons);
            allAddresses.add(individualAddress);
        }
        addressDisplayer.put("addresses",allAddresses);
        logger.debug("Returned data"+addressDisplayer);
        return addressDisplayer;

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