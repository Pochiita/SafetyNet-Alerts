package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.model.JsonReader;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    @DeleteMapping("/{id}")
    public List deletePersons(@PathVariable int id){
        List persons = jsonElts.getPersons();
        System.out.println(persons.size());
        if (id >=0 && id < persons.size()){
            persons.remove(id);
            System.out.println(persons.size());

        }
        return persons;
    }
}
