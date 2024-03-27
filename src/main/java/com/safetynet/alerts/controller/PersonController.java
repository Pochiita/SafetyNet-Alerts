package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.ChildAlertCountDTO;
import com.safetynet.alerts.dto.CommunityMailDTO;
import com.safetynet.alerts.dto.PersonInfoCountDTO;
import com.safetynet.alerts.dto.PhoneAlertDTO;
import com.safetynet.alerts.services.PersonServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.text.ParseException;

@RestController
@RequestMapping
public class PersonController {

    @Autowired
    private PersonServices personServices;

    Logger logger = LoggerFactory.getLogger(PersonController.class);
    @DeleteMapping("/person/{name}")
    public ResponseEntity<String> deletePersons(@PathVariable String name) throws IOException {
        ResponseEntity<String> response =  personServices.deletePersons(name);

        if (response.getStatusCode().is2xxSuccessful()){
            String msg = " 'Person' correctly deleted";
            logger.info(msg);
        }else{
            String msg = " 'Person' couldn't be deleted";
            logger.error(msg);
        }
        return response;
    }

    @PutMapping("/person/{name}")
    public ResponseEntity<String> modifyPerson(@PathVariable String name, @RequestParam(value = "city") String cityName, @RequestParam(value = "address") String address, @RequestParam(value = "zip") String zip, @RequestParam(value = "phone") String phone, @RequestParam(value = "mail") String mail) throws IOException {
        ResponseEntity<String> response = personServices.modifyPerson(name, cityName, address, zip, phone, mail);

        if (response.getStatusCode().is2xxSuccessful()){
            String msg = " 'Person' correctly modified";
            logger.info(msg);
        }else{
            String msg = " 'Person' couldn't be modified";
            logger.error(msg);
        }
        return response;
    }


    @PostMapping("/person")
    public ResponseEntity<String> createPerson(@RequestParam(value = "firstName") String firstName, @RequestParam(value = "lastName") String lastName, @RequestParam(value = "city") String cityName, @RequestParam(value = "address") String address, @RequestParam(value = "zip") String zip, @RequestParam(value = "phone") String phone, @RequestParam(value = "mail") String mail) throws IOException {
        ResponseEntity<String> response = personServices.createPerson(firstName, lastName, cityName, address, zip, phone, mail);

        if (response.getStatusCode().is2xxSuccessful()){
            String msg = " 'Person' correctly created";
            logger.info(msg);
        }else{
            String msg = " 'Person' couldn't be created";
            logger.error(msg);
        }
        return response;
    }

    @GetMapping("/childAlert")
    public ChildAlertCountDTO childAlert(@RequestParam(value = "address") String address) throws IOException, ParseException {
        ChildAlertCountDTO response = personServices.childAlert(address);
        logger.debug("Returned data :" + response);
        return response;
    }

    @GetMapping("/phoneAlert")
    public PhoneAlertDTO phonedAlert(@RequestParam(value = "firestation") String station) throws IOException, ParseException {
        PhoneAlertDTO response = personServices.phonedAlert(station);
        logger.debug("Returned data :" + response);
        return response;
    }

    @GetMapping("/personinfo")
    public PersonInfoCountDTO personInfo(@RequestParam(value = "firstName") String firstName, @RequestParam(value = "lastName") String lastName) throws IOException, ParseException {
        PersonInfoCountDTO response =  personServices.personInfo(firstName, lastName);
        logger.debug("Returned data :" + response);
        return response;
    }

    @GetMapping("/communityEmail")
    public CommunityMailDTO communityEmail(@RequestParam(value = "city") String city) throws IOException, ParseException {
        CommunityMailDTO response =  personServices.communityEmail(city);
        logger.debug("Returned data :" + response);
        return response;
    }

}