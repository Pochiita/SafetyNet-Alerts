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
    public List<HashMap> PeopleConcernedByStation (@RequestParam(value ="stationNumber") String stationNumber) throws IOException, ParseException {

        List<HashMap> returningData = new ArrayList<>();
        HashMap<String,List<HashMap>> rowPersons = new HashMap<>();
        HashMap<String,Integer> personsCounting = new HashMap<>();
        List<HashMap> allReturnedPersons = new ArrayList<>();
        ListSearcher listSearcher = new ListSearcher();

        List<FireStation> fireStationsByStation = listSearcher.searchAFireStationByValue("station",stationNumber,sharedJson().getFirestations());
        List<Person> allPersons = sharedJson().getPersons();
        int adultNbr = 0;
        int minorNbr = 0;

        for (FireStation a : fireStationsByStation){
            String actualAddress = a.getAddress();
            for (Person b:allPersons){
                if (b.getAddress().equals(actualAddress)){
                    HashMap<String,String> singlePerson = new HashMap<>();
                    singlePerson.put("firstName",b.getFirstName());
                    singlePerson.put("lastName",b.getLastName());
                    singlePerson.put("address",b.getAddress());
                    singlePerson.put("phone number",b.getPhone());
                    allReturnedPersons.add(singlePerson);

                    String firstName = b.getFirstName();
                    String lastName = b.getLastName();
                    String fullName = firstName.concat(lastName).toLowerCase();
                    MedicalRecord currentMedical = listSearcher.searchAMecialRecordInAList(fullName,sharedJson().getMedicalrecords());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy");
                    LocalDate dob = LocalDate.parse(currentMedical.getBirthdate(),formatter);
                    LocalDate now = LocalDate.now();
                    Period period = Period.between(dob, LocalDate.now());

                   if(period.getYears()>=18){
                       adultNbr++;
                   }else{
                       minorNbr++;
                   }

                }
            }
        }


        rowPersons.put("persons",allReturnedPersons);
        personsCounting.put("adults",adultNbr);
        personsCounting.put("minors",minorNbr);
        returningData.add(rowPersons);
        returningData.add(personsCounting);
        return returningData;
    }
}
