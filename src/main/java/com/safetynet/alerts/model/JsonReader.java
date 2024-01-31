package com.safetynet.alerts.model;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.parser.*;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class JsonReader {

    private final ObjectMapper objectMapper;
    public JsonReader() throws IOException, ParseException {
       this.objectMapper = new ObjectMapper();

    }

    public JsonElts getWholeJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonElts jsonElts = objectMapper.readValue(new File("src/main/resources/data.json"), JsonElts.class);
        return jsonElts;
    }






}