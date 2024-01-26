package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.model.JsonReader;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping(path="/person")

public class CrudController {

    private JsonElts jsonElts;

    public CrudController() throws IOException, ParseException {
        JsonReader jsonReader = new JsonReader();
        this.jsonElts = jsonReader.getWholeJson();
    }
    @GetMapping
    public List getPersons (){
        return jsonElts.getPersons();
    }

    @DeleteMapping("/{name}")
    public List deletePersons(@PathVariable String name){
        List persons = jsonElts.getPersons();
        String toCompare = name.toLowerCase();
        int index = 0;
        for (Object attributes:persons) {
            LinkedHashMap<String,String> mapPerson = (LinkedHashMap<String, String>) attributes;
            String firstName = mapPerson.get("firstName");
            String lastName = mapPerson.get("lastName");
            String fullName = firstName.concat(lastName).toLowerCase();
            if (toCompare.equals(fullName)){
                break;
            }
            index++;
        }
        persons.remove(index);
        return persons;
    }
}
