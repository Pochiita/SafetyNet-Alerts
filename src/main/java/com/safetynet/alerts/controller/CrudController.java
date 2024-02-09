package com.safetynet.alerts.controller;
import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.model.JsonReader;
import com.safetynet.alerts.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping
public class CrudController {
@Autowired
    private JsonReader jsonReader;

    Logger logger = LoggerFactory.getLogger(JsonElts.class);


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
        Person selectedPerson = getPerson(name);
        if (selectedPerson != null) {
            selectedPerson.setCity(cityName);
            selectedPerson.setAddress(address);
            selectedPerson.setZip(zip);
            selectedPerson.setPhone(phone);
            selectedPerson.setEmail(mail);
            String msg = " 'Person' correctly modified - Value:".concat(name);
            logger.info(msg);
            return new ResponseEntity<>("Not modified", HttpStatus.BAD_REQUEST);

        }else{
            String msg = " 'Person' couldn't be modified - Not found by value :".concat(name);
            logger.info(msg);
        }
        return new ResponseEntity<>("Correctly modified", HttpStatus.OK);

    }
    private Person getPerson(String name) throws IOException {
        List<Person> persons = sharedJson().getPersons();
        String toCompare = name.toLowerCase();
        int index = 0;
        Person selectedPerson = null;
        for (Person person:persons) {
            String firstName = person.getFirstName();
            String lastName = person.getLastName();
            String fullName = firstName.concat(lastName).toLowerCase();
            if (toCompare.equals(fullName)){
                selectedPerson = persons.get(index);
            }
            index++;
        }
        return selectedPerson;
    }

    @PostMapping("/person/")
    public ResponseEntity<String> modifyPerson (@RequestParam(value ="firstName") String firstName,@RequestParam(value ="lastName") String lastName,@RequestParam(value ="city") String cityName,@RequestParam(value="address") String address,@RequestParam(value="zip") String zip,@RequestParam(value="phone") String phone,@RequestParam(value="mail") String mail) throws IOException {
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






}
