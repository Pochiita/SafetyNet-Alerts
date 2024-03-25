package com.safetynet.alerts.controller;

import com.safetynet.alerts.services.PersonServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

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
    public ResponseEntity<String> modifyPerson (@PathVariable String name,@RequestParam(value ="city") String cityName,@RequestParam(value="address") String address,@RequestParam(value="zip") String zip,@RequestParam(value="phone") String phone,@RequestParam(value="mail") String mail) throws IOException {
        return personServices.modifyPerson(name, cityName, address, zip, phone, mail);
    }


    @PostMapping("/person")
    public ResponseEntity<String> createPerson (@RequestParam(value ="firstName") String firstName,@RequestParam(value ="lastName") String lastName,@RequestParam(value ="city") String cityName,@RequestParam(value="address") String address,@RequestParam(value="zip") String zip,@RequestParam(value="phone") String phone,@RequestParam(value="mail") String mail) throws IOException {
        return personServices.createPerson(firstName, lastName, cityName, address, zip, phone, mail);
    }
}
