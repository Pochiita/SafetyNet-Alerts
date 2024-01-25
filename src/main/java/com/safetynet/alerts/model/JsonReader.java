package com.safetynet.alerts.model;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
        return objectMapper.readValue(new File("src/main/resources/data.json"),JsonElts.class);
    }





}