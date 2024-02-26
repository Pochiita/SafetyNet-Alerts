package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.services.JsonReader;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.services.ListSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
public class FrontController {


    @Autowired
    private JsonReader jsonReader;

    public JsonElts sharedJson() throws IOException {
        return jsonReader.getJson();
    }

    @GetMapping("/firestation")
    public List<HashMap> PeopleConcernedByStation(@RequestParam(value = "stationNumber") String stationNumber) throws IOException, ParseException {

        List<HashMap> returningData = new ArrayList<>();
        HashMap<String, List<HashMap>> rowPersons = new HashMap<>();
        HashMap<String, Integer> personsCounting = new HashMap<>();
        List<HashMap> allReturnedPersons = new ArrayList<>();
        ListSearcher listSearcher = new ListSearcher();

        List<FireStation> fireStationsByStation = listSearcher.searchAFireStationByValue("station", stationNumber, sharedJson().getFirestations());
        if (fireStationsByStation.isEmpty()) {
            return returningData;
        }
        List<Person> allPersons = sharedJson().getPersons();
        int adultNbr = 0;
        int minorNbr = 0;

        for (FireStation a : fireStationsByStation) {
            String actualAddress = a.getAddress();
            for (Person b : allPersons) {
                if (b.getAddress().equals(actualAddress)) {
                    HashMap<String, String> singlePerson = new HashMap<>();
                    singlePerson.put("firstName", b.getFirstName());
                    singlePerson.put("lastName", b.getLastName());
                    singlePerson.put("address", b.getAddress());
                    singlePerson.put("phone number", b.getPhone());
                    allReturnedPersons.add(singlePerson);

                    String firstName = b.getFirstName();
                    String lastName = b.getLastName();
                    String fullName = firstName.concat(lastName).toLowerCase();
                    MedicalRecord currentMedical = listSearcher.searchAMecialRecordInAList(fullName, sharedJson().getMedicalrecords());
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


        rowPersons.put("persons", allReturnedPersons);
        personsCounting.put("adults", adultNbr);
        personsCounting.put("minors", minorNbr);
        returningData.add(rowPersons);
        returningData.add(personsCounting);
        return returningData;
    }

    @GetMapping("/childAlert")
    public List<HashMap> childAlert(@RequestParam(value = "address") String address) throws IOException, ParseException {

        //Setup variables that displays the json correctly
        List<HashMap> returningData = new ArrayList<>();
        HashMap<String, List<HashMap>> minorsList = new HashMap<>();
        HashMap<String, List<HashMap>> otherResidents = new HashMap<>();
        List<HashMap> allMinors = new ArrayList<>();
        List<HashMap> allAdults = new ArrayList<>();

        //Instanciate methods that allow to find all Person by address
        ListSearcher listSearcher = new ListSearcher();
        List<Person> listResidents = listSearcher.searchAPersonInAListByAddress(address, sharedJson().getPersons());

        //Return empty json if we have no one at this address
        if (listResidents.isEmpty()){
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
            MedicalRecord currentPersonMedicalRecord = listSearcher.searchAMecialRecordInAList(fullName, sharedJson().getMedicalrecords());
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
                MedicalRecord currentPersonMedicalRecord = listSearcher.searchAMecialRecordInAList(fullName, sharedJson().getMedicalrecords());
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

        return returningData;
    }
}