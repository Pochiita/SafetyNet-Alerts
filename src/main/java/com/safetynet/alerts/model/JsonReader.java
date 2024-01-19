package com.safetynet.alerts.model;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class JsonReader {

    private final Object obj;

    public JsonReader() throws IOException, ParseException {
        this.obj = new JSONParser().parse(new FileReader("src/main/resources/data.json"));
    }

    public Map getWholeJson() {
        return (JSONObject) obj;
    }

    public JSONArray getFireStations (){
        return (JSONArray) getWholeJson().get("firestations");
    }

    public FireStations populateFireStations (){
        FireStations fireStations = new FireStations();
        JSONArray fireStationsList = getFireStations();
        fireStationsList.forEach(item->{
            JSONObject singleStation = (JSONObject) item;
            HashMap<String,String> stations = new HashMap<String,String>();
            stations.put(singleStation.get("address").toString(),singleStation.get("station").toString());
            System.out.println(stations);
        });
        return fireStations;
    }

}