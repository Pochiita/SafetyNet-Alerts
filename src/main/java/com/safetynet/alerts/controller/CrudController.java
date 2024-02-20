package com.safetynet.alerts.controller;
import com.safetynet.alerts.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping
public class CrudController {
@Autowired
    private JsonReader jsonReader;

    Logger logger = LoggerFactory.getLogger(JsonElts.class);

    ListSearcher listSearcher = new ListSearcher();

    public JsonElts sharedJson() throws IOException {
        return jsonReader.getJson();
    }


    @DeleteMapping("/person/{name}")
    public ResponseEntity<String> deletePersons(@PathVariable String name) throws IOException {

        List<Person> people = sharedJson().getPersons();
        String toCompare = name.toLowerCase();
        int index = 0;
        boolean canDelete = false;
        for (Person person:people) {
            String firstName = person.getFirstName();
            String lastName = person.getLastName();
            String fullName = firstName.concat(lastName).toLowerCase();
            if (toCompare.equals(fullName)){
                canDelete = true;
               break;
            }

            index++;
        }
        if (canDelete) {
            people.remove(index);
            sharedJson().setPersons(people);
            String msg = " 'Person' correctly deleted - Value:".concat(name);
            logger.info(msg);

        }else{
            String msg = " 'Person' couldn't be deleted - Not found by value :".concat(name);
            logger.error(msg);
            return new ResponseEntity<>("Not deleted", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Correctly deleted",HttpStatus.OK);
    }

    @PutMapping("/person/{name}")
    public ResponseEntity<String> modifyPerson (@PathVariable String name,@RequestParam(value ="city") String cityName,@RequestParam(value="address") String address,@RequestParam(value="zip") String zip,@RequestParam(value="phone") String phone,@RequestParam(value="mail") String mail) throws IOException {
        List<Person> persons = sharedJson().getPersons();
        Person selectedPerson = listSearcher.searchAPersonInAList(name,persons);
        if (selectedPerson != null) {
            selectedPerson.setCity(cityName);
            selectedPerson.setAddress(address);
            selectedPerson.setZip(zip);
            selectedPerson.setPhone(phone);
            selectedPerson.setEmail(mail);
            String msg = " 'Person' correctly modified - Value:".concat(name);
            logger.info(msg);
            return new ResponseEntity<>("Correctly modified", HttpStatus.OK);

        }else{
            String msg = " 'Person' couldn't be modified - Not found by value :".concat(name);
            logger.info(msg);
            return new ResponseEntity<>("Not modified", HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/person/")
    public ResponseEntity<String> createPerson (@RequestParam(value ="firstName") String firstName,@RequestParam(value ="lastName") String lastName,@RequestParam(value ="city") String cityName,@RequestParam(value="address") String address,@RequestParam(value="zip") String zip,@RequestParam(value="phone") String phone,@RequestParam(value="mail") String mail) throws IOException {
        List<Person> persons = sharedJson().getPersons();
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
        if (persons.size()>listSize){
            String msg = " 'Person' correctly created - 'Person' :".concat(firstName).concat(lastName);
            logger.info(msg);
        }else{
            String msg = " 'Person' couldn't be created";
            logger.info(msg);
            return new ResponseEntity<>("Not created", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Correctly created", HttpStatus.OK);
    }


    /**
     * FireStations
     */

    @PostMapping("/firestation/")
    public ResponseEntity<String> createFirestation (@RequestParam(value ="address") String address,@RequestParam(value ="station") String station) throws IOException {
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
            logger.info(msg);
            return new ResponseEntity<>("Not created", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Correctly created", HttpStatus.OK);
    }


    @PutMapping("/firestation/{address}")
    public ResponseEntity<String> modifyStation (@PathVariable String address,@RequestParam(value="station") String station) throws IOException {
        List<FireStation> fireStations = sharedJson().getFirestations();
        FireStation selectedFirestation = listSearcher.searchAFireStationByAddress(address,fireStations);
        if (selectedFirestation != null) {
            selectedFirestation.setStation(station);
            String msg = " 'Firestation' correctly modified - New station number:".concat(selectedFirestation.getStation());
            logger.info(msg);
            return new ResponseEntity<>("Correctly modified", HttpStatus.OK);

        }else{
            String msg = " 'Firestation' couldn't be modified - Not found by value :".concat(address);
            logger.info(msg);
            return new ResponseEntity<>("Not modified", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/firestation")
    public ResponseEntity<String> deleteFirestations(@RequestParam(value="queryby") String param, @RequestParam(value="option") String option) throws IOException {

        List<FireStation> fireStations = sharedJson().getFirestations();
        int index = 0;
        boolean canDelete = false;
        if (!Objects.equals(param, "address") && !Objects.equals(param, "station")){
            return new ResponseEntity<>("Incorrect value 'queryby' must be either 'address' or 'station' ", HttpStatus.BAD_REQUEST);
        }

        if (Objects.equals(param, "station")){
            if (!option.matches("\\d+")){
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
            return new ResponseEntity<>("No Firestation selected",HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Correctly deleted",HttpStatus.OK);
    }


    /**
     * MedicalRecords
     */

    @PostMapping("/medicalrecords")
    public ResponseEntity<String> createMedicalRecord (@RequestParam(value ="firstName") String firstName,@RequestParam(value ="lastName") String lastName,@RequestParam(value ="birthdate") String birthDate,@RequestParam(value="medication") List<String> medication,@RequestParam(value="allergies") List<String> allergies) throws IOException {
        List<MedicalRecord> medicalRecordsList = sharedJson().getMedicalrecords();
        int listSize = medicalRecordsList.size();
        MedicalRecord newMedicalRecord = new MedicalRecord();
        String[] checkingBirthDate = birthDate.split("/");
        int index = 0;
        for(String a : checkingBirthDate){
            if (!a.matches("\\d+")) {
                return new ResponseEntity<>("Birthdate must only contains digits",HttpStatus.BAD_REQUEST);
            }
            index++;
        }
        newMedicalRecord.setFirstName(firstName);
        newMedicalRecord.setLastName(lastName);
        newMedicalRecord.setBirthDate(birthDate);
        newMedicalRecord.setMedications(medication);
        newMedicalRecord.setAllergies(allergies);
        medicalRecordsList.add(newMedicalRecord);
        if (medicalRecordsList.size()>listSize){
            String msg = " 'MedicalRecord' correctly created - 'MedicalRecord' :".concat(firstName).concat(lastName);
            logger.info(msg);
        }else{
            String msg = " 'MedicalRecord' couldn't be created";
            logger.info(msg);
            return new ResponseEntity<>("Not created", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Correctly created", HttpStatus.OK);
    }

    @DeleteMapping("/medicalrecords/{name}")
    public ResponseEntity<String> deleteMedicalRecord(@PathVariable String name) throws IOException {

        List<MedicalRecord> medicalRecordList = sharedJson().getMedicalrecords();
        String toCompare = name.toLowerCase();
        int index = 0;
        boolean canDelete = false;
        for (MedicalRecord medicalRecordSingle:medicalRecordList) {
            String firstName = medicalRecordSingle.getFirstName();
            String lastName = medicalRecordSingle.getLastName();
            String fullName = firstName.concat(lastName).toLowerCase();
            if (toCompare.equals(fullName)){
                canDelete = true;
                break;
            }

            index++;
        }
        if (canDelete) {
            medicalRecordList.remove(index);
            sharedJson().setMedicalrecords(medicalRecordList);
            String msg = " 'MedicalRecord' correctly deleted - Value:".concat(name);
            logger.info(msg);

        }else{
            String msg = " 'MedicalRecord' couldn't be deleted - Not found by value :".concat(name);
            logger.error(msg);
            return new ResponseEntity<>("Not deleted", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Correctly deleted",HttpStatus.OK);
    }


}
