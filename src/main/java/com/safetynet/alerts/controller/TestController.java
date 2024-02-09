package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.model.JsonReader;
import com.safetynet.alerts.model.Person;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {


    @Autowired
    private JsonReader jsonReader;

    public JsonElts sharedJson() throws IOException {
        return jsonReader.getJson();
    }

    @GetMapping("/person")
    public List<Person> modifyPerson () throws IOException {
        System.out.println(sharedJson().getPersons().size());
        return sharedJson().getPersons();
    }
}
