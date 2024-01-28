package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.model.JsonReader;
import com.safetynet.alerts.model.Person;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping

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

    @DeleteMapping("/person/{name}")
    public List<Person> deletePersons(@PathVariable String name){
        System.out.println(jsonElts.getPersons().getClass());

        List<Person> people = jsonElts.getPersons();
        System.out.println(people);
        for(Person person: people){
            System.out.println(person.getAddress());
        }
        return people;
        /*
        String toCompare = name.toLowerCase();
        int index = 0;
        for (Person person:persons) {
            String firstName = person.getFirstName();
            String lastName = person.getLastName();
            String fullName = firstName.concat(lastName).toLowerCase();
            if (toCompare.equals(fullName)){
                break;
            }
            index++;
        }
        persons.remove(index);*/
    }


}
