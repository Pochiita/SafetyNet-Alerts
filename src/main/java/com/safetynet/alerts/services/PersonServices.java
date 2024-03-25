package com.safetynet.alerts.services;

import com.safetynet.alerts.model.JsonElts;
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
            allElements.getJson().setPersons(people);
            String msg = " 'Person' correctly deleted - Value:".concat(name);
            logger.info(msg);

        }else{
            String msg = " 'Person' couldn't be deleted - Not found by value :".concat(name);
            logger.error(msg);
            return new ResponseEntity<>("Not deleted", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Correctly deleted",HttpStatus.OK);
    }


    public ResponseEntity<String> modifyPerson (@PathVariable String name, @RequestParam(value ="city") String cityName, @RequestParam(value="address") String address, @RequestParam(value="zip") String zip, @RequestParam(value="phone") String phone, @RequestParam(value="mail") String mail) throws IOException {
        logger.info(urlLogger());
        List<Person> persons = allElements.getJson().getPersons();
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
            logger.error(msg);
            return new ResponseEntity<>("Not modified", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> createPerson (@RequestParam(value ="firstName") String firstName,@RequestParam(value ="lastName") String lastName,@RequestParam(value ="city") String cityName,@RequestParam(value="address") String address,@RequestParam(value="zip") String zip,@RequestParam(value="phone") String phone,@RequestParam(value="mail") String mail) throws IOException {
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
        if (persons.size()>listSize){
            String msg = " 'Person' correctly created - 'Person' :".concat(firstName).concat(lastName);
            logger.info(msg);
        }else{
            String msg = " 'Person' couldn't be created";
            logger.error(msg);
            return new ResponseEntity<>("Not created", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Correctly created", HttpStatus.OK);
    }
}
