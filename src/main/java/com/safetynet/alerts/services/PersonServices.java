package com.safetynet.alerts.services;

import com.safetynet.alerts.dto.*;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.JsonElts;
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

@Service
public class PersonServices {


    @Autowired
    private JsonReader allElements;

    Logger logger = LoggerFactory.getLogger(PersonServices.class);

    ListSearcher listSearcher = new ListSearcher();

    public String urlLogger() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String url = request.getRequestURL().toString();
        String ipAddress = request.getRemoteAddr();
        return "The URL used to access this route is: " + url + " and the IP address is: " + ipAddress;
    }

    public ResponseEntity<String> deletePersons(@PathVariable String name) throws IOException {
        logger.info(urlLogger());
        List<Person> people = allElements.getJson().getPersons();
        String toCompare = name.toLowerCase();
        int index = 0;
        boolean canDelete = false;
        for (Person person : people) {
            String firstName = person.getFirstName();
            String lastName = person.getLastName();
            String fullName = firstName.concat(lastName).toLowerCase();
            if (toCompare.equals(fullName)) {
                canDelete = true;
                break;
            }

            index++;
        }
        if (canDelete) {
            people.remove(index);
            allElements.getJson().setPersons(people);
        } else {
            return new ResponseEntity<>("Not deleted", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Correctly deleted", HttpStatus.OK);
    }


    public ResponseEntity<String> modifyPerson(@PathVariable String name, @RequestParam(value = "city") String cityName, @RequestParam(value = "address") String address, @RequestParam(value = "zip") String zip, @RequestParam(value = "phone") String phone, @RequestParam(value = "mail") String mail) throws IOException {
        logger.info(urlLogger());
        List<Person> persons = allElements.getJson().getPersons();
        Person selectedPerson = listSearcher.searchAPersonInAList(name, persons);
        if (selectedPerson != null) {
            selectedPerson.setCity(cityName);
            selectedPerson.setAddress(address);
            selectedPerson.setZip(zip);
            selectedPerson.setPhone(phone);
            selectedPerson.setEmail(mail);
            return new ResponseEntity<>("Correctly modified", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Not modified", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> createPerson(@RequestParam(value = "firstName") String firstName, @RequestParam(value = "lastName") String lastName, @RequestParam(value = "city") String cityName, @RequestParam(value = "address") String address, @RequestParam(value = "zip") String zip, @RequestParam(value = "phone") String phone, @RequestParam(value = "mail") String mail) throws IOException {
        logger.info(urlLogger());
        List<Person> persons = allElements.getJson().getPersons();
        int listSize = persons.size();
        Person newPerson = new Person();
        newPerson.setFirstName(firstName);
        newPerson.setLastName(lastName);
        newPerson.setCity(cityName);
        newPerson.setAddress(address);
        newPerson.setZip(zip);
        newPerson.setPhone(phone);
        newPerson.setEmail(mail);
        persons.add(newPerson);
        if (persons.size() == listSize) {
            return new ResponseEntity<>("Not created", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Correctly created", HttpStatus.OK);
    }

    public ChildAlertCountDTO childAlert(@RequestParam(value = "address") String address) throws IOException, ParseException {
        logger.info(urlLogger());
        //Instanciate methods that allow to find all Person by address
        ListSearcher listSearcher = new ListSearcher();
        List<Person> listResidents = listSearcher.searchPersonsInAListByAddress(address, allElements.getJson().getPersons());

        //Return empty json if we have no one at this address
        if (listResidents.isEmpty()) {
            logger.error("There is no residents for the address : " + address + " or the address doesn't exist");

            return new ChildAlertCountDTO(new ArrayList<>(), new ArrayList<>());
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
            MedicalRecord currentPersonMedicalRecord = listSearcher.searchAMedicalRecordInAList(fullName, allElements.getJson().getMedicalrecords());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy");
            LocalDate dob = LocalDate.parse(currentPersonMedicalRecord.getBirthdate(), formatter);
            Period period = Period.between(dob, LocalDate.now());
            if (period.getYears() <= 18) {
                ChildAlertDTO singleMinor = new ChildAlertDTO(c.getFirstName(), c.getLastName(), period.getYears());
                minorsList.add(singleMinor);
                minorsIndex.add(index);
            }
            index++;
        }

        //If there is no minors in the return empty json
        if (minorsList.isEmpty()) {
            logger.error("There is not minor Person at this address");
            return new ChildAlertCountDTO(new ArrayList<>(), new ArrayList<>());
        }


        //Loop over all the residents again except we are going to skip the minors to only add adults to our hashmap
        int indexA = 0;

        for (Person c : listResidents) {
            if (!minorsIndex.contains(indexA)) {

                String firstName = c.getFirstName();
                String lastName = c.getLastName();
                String fullName = firstName.concat(lastName).toLowerCase();
                MedicalRecord currentPersonMedicalRecord = listSearcher.searchAMedicalRecordInAList(fullName, allElements.getJson().getMedicalrecords());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy");
                LocalDate dob = LocalDate.parse(currentPersonMedicalRecord.getBirthdate(), formatter);
                Period period = Period.between(dob, LocalDate.now());
                ChildAlertDTO singleAdult = new ChildAlertDTO(c.getFirstName(), c.getLastName(), period.getYears());
                adultList.add(singleAdult);
            }
            indexA++;
        }
        return new ChildAlertCountDTO(minorsList, adultList);
    }


    public PhoneAlertDTO phonedAlert(@RequestParam(value = "firestation") String station) throws IOException, ParseException {
        logger.info(urlLogger());
        ListSearcher listSearcher = new ListSearcher();
        List<FireStation> firestationsConcerned = listSearcher.searchAFireStationByValue("station", station, allElements.getJson().getFirestations());
        List<String> firestationsAddresses = new ArrayList<>();

        for (FireStation a : firestationsConcerned) {
            firestationsAddresses.add(a.getAddress());
        }

        List<String> phonesList = new ArrayList<>();
        List<Person> allPersons = allElements.getJson().getPersons();
        for (Person a : allPersons) {
            if (firestationsAddresses.contains(a.getAddress())) {
                logger.debug("Adding people's phone number who match the given address");
                phonesList.add(a.getPhone());
            }
        }
        return new PhoneAlertDTO(phonesList);
    }

    public PersonInfoCountDTO personInfo(@RequestParam(value = "firstName") String firstName, @RequestParam(value = "lastName") String lastName) throws IOException, ParseException {
        logger.info(urlLogger());

        List<Person> allPersons = allElements.getJson().getPersons();
        ListSearcher listSearcher = new ListSearcher();
        List<Person> selectedPersons = listSearcher.searchPersonsInAListByName(firstName.toLowerCase() + lastName.toLowerCase(), allPersons);
        List<PersonInfoDTO> concernedPersons = new ArrayList<>();
        for (Person a : selectedPersons) {

            String firstNameVar = a.getFirstName();
            String lastNameVar = a.getLastName();
            String fullName = firstNameVar.concat(lastNameVar).toLowerCase();
            MedicalRecord currentMedicalRecord = listSearcher.searchAMedicalRecordInAList(fullName, allElements.getJson().getMedicalrecords());
            //Here we get the age by checking the difference between Today and birthdate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy");
            LocalDate dob = LocalDate.parse(currentMedicalRecord.getBirthdate(), formatter);
            Period period = Period.between(dob, LocalDate.now());
            List<List<String>> medicationAllergies = new ArrayList<>();
            medicationAllergies.add(currentMedicalRecord.getAllergies());
            medicationAllergies.add(currentMedicalRecord.getMedications());
            concernedPersons.add(new PersonInfoDTO(a.getLastName(), period.getYears(), a.getEmail(), medicationAllergies));
        }

        PersonInfoCountDTO returnedData = new PersonInfoCountDTO(concernedPersons);
        return returnedData;
    }

    public CommunityMailDTO communityEmail(@RequestParam(value = "city") String city) throws IOException, ParseException {
        logger.info(urlLogger());
        List<String> allMails = new ArrayList<>();

        ListSearcher listSearcher = new ListSearcher();

        List<Person> personByCity = listSearcher.searchPersonsInAListByCity(city, allElements.getJson().getPersons());
        for (Person a : personByCity) {
            allMails.add(a.getEmail());
        }
        return new CommunityMailDTO(allMails);
    }
}