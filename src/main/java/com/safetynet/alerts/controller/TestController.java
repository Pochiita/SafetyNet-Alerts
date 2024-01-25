package com.safetynet.alerts.controller;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.safetynet.alerts.model.JsonReader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="test")
public class TestController {

    private final JsonReader jsonReader;

    public TestController(JsonReader jsonReader){
        this.jsonReader= jsonReader;
    }

}
