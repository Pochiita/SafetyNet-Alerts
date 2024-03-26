package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.ChildAlertCountDTO;
import com.safetynet.alerts.dto.CommunityMailDTO;
import com.safetynet.alerts.dto.PersonInfoCountDTO;
import com.safetynet.alerts.dto.PhoneAlertDTO;
import com.safetynet.alerts.services.PersonServices;
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


    @DeleteMapping("/person/{name}")
    public ResponseEntity<String> deletePersons(@PathVariable String name) throws IOException {
        return personServices.deletePersons(name);
    }

    @PutMapping("/person/{name}")
    public ResponseEntity<String> modifyPerson(@PathVariable String name, @RequestParam(value = "city") String cityName, @RequestParam(value = "address") String address, @RequestParam(value = "zip") String zip, @RequestParam(value = "phone") String phone, @RequestParam(value = "mail") String mail) throws IOException {
        return personServices.modifyPerson(name, cityName, address, zip, phone, mail);
    }


    @PostMapping("/person")
    public ResponseEntity<String> createPerson(@RequestParam(value = "firstName") String firstName, @RequestParam(value = "lastName") String lastName, @RequestParam(value = "city") String cityName, @RequestParam(value = "address") String address, @RequestParam(value = "zip") String zip, @RequestParam(value = "phone") String phone, @RequestParam(value = "mail") String mail) throws IOException {
        return personServices.createPerson(firstName, lastName, cityName, address, zip, phone, mail);
    }

    @GetMapping("/childAlert")
    public ChildAlertCountDTO childAlert(@RequestParam(value = "address") String address) throws IOException, ParseException {
        return personServices.childAlert(address);
    }

    @GetMapping("/phoneAlert")
    public PhoneAlertDTO phonedAlert(@RequestParam(value = "firestation") String station) throws IOException, ParseException {
        return personServices.phonedAlert(station);
    }

    @GetMapping("/personinfo")
    public PersonInfoCountDTO personInfo(@RequestParam(value = "firstName") String firstName, @RequestParam(value = "lastName") String lastName) throws IOException, ParseException {
        return personServices.personInfo(firstName, lastName);
    }

    @GetMapping("/communityEmail")
    public CommunityMailDTO communityEmail(@RequestParam(value = "city") String city) throws IOException, ParseException {
        return personServices.communityEmail(city);
    }

}