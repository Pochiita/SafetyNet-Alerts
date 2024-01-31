package com.safetynet.alerts.controller;
import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.model.JsonReader;
import com.safetynet.alerts.model.Person;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class CrudController {

    private JsonElts jsonElts;

    public CrudController() throws IOException, ParseException {
        JsonReader jsonReader = new JsonReader();
        this.jsonElts = jsonReader.getWholeJson();

    }


    @DeleteMapping("/person/{name}")
    public void deletePersons(@PathVariable String name){

        List<Person> people = jsonElts.getPersons();
        String toCompare = name.toLowerCase();
        int index = 0;
        boolean canDelete = false;
        for (Person person:people) {
            String firstName = person.getFirstName();
            String lastName = person.getLastName();
            String fullName = firstName.concat(lastName).toLowerCase();
            if (toCompare.equals(fullName)){
                canDelete = true;
               break;
            }

            index++;
        }
        if (canDelete) {
            people.remove(index);
            jsonElts.setPersons(people);
        }else{
            return;
        }
    }

    @PutMapping("/person/{name}")
    public void modifyPerson (@PathVariable String name,@RequestParam(value ="city") String cityName,@RequestParam(value="address") String address,@RequestParam(value="zip") String zip,@RequestParam(value="phone") String phone,@RequestParam(value="mail") String mail){
        Person selectedPerson = getPerson(name);
        if (selectedPerson != null) {
            selectedPerson.setCity(cityName);
            selectedPerson.setAddress(address);
            selectedPerson.setZip(zip);
            selectedPerson.setPhone(phone);
            selectedPerson.setEmail(mail);
        }
    }
    private Person getPerson(String name) {
        List<Person> persons = jsonElts.getPersons();
        String toCompare = name.toLowerCase();
        int index = 0;
        Person selectedPerson = null;
        for (Person person:persons) {
            String firstName = person.getFirstName();
            String lastName = person.getLastName();
            String fullName = firstName.concat(lastName).toLowerCase();
            if (toCompare.equals(fullName)){
                selectedPerson = persons.get(index);
            }
            index++;
        }
        return selectedPerson;
    }

    @PostMapping("/person/")
    public void modifyPerson (@RequestParam(value ="firstName") String firstName,@RequestParam(value ="lastName") String lastName,@RequestParam(value ="city") String cityName,@RequestParam(value="address") String address,@RequestParam(value="zip") String zip,@RequestParam(value="phone") String phone,@RequestParam(value="mail") String mail) {
        List<Person> persons = jsonElts.getPersons();
        Person newPerson = new Person();
        newPerson.setFirstName(firstName);
        newPerson.setLastName(lastName);
        newPerson.setCity(cityName);
        newPerson.setAddress(address);
        newPerson.setZip(zip);
        newPerson.setPhone(phone);
        newPerson.setEmail(mail);
        persons.add(newPerson);
    }






}
