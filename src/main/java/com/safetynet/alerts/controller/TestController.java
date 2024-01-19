package com.safetynet.alerts.controller;
import com.safetynet.alerts.model.FireStations;
import com.safetynet.alerts.model.JsonReader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="test")
public class TestController {

    private final JsonReader jsonReader;

    public TestController(JsonReader jsonReader){
        this.jsonReader= jsonReader;
    }
    @GetMapping(path="string")
    public List getString(){
         List test = jsonReader.getFireStations();
         jsonReader.populateFireStations();
        //return "hello";
        return test;
    }
}
