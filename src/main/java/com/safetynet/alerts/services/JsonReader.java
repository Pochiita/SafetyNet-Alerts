package com.safetynet.alerts.services;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.JsonElts;
import jakarta.annotation.PostConstruct;
import org.json.simple.parser.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class JsonReader {

    private final ObjectMapper objectMapper;
    private JsonElts jsonElts;
    public JsonReader() throws IOException, ParseException {
       this.objectMapper = new ObjectMapper();

    }
    @PostConstruct
    public void getWholeJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        this.jsonElts = objectMapper.readValue(new File("src/main/resources/data.json"), JsonElts.class);
        return ;
    }

    public JsonElts getJson () throws IOException {
        return  jsonElts;
    }






}