package com.safetynet.alerts.model;

import java.util.List;

public class ListSearcher {


    public Person searchAPersonInAList (String name,List<Person> list){
        List<Person> persons = list;
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

    public FireStation searchAFireStationByAddress(String address,List<FireStation> list){
        List<FireStation> firestations = list;
        String toCompare = address.toLowerCase().replaceAll("\\s","");
        int index = 0;
        FireStation selectedFirestation = null;
        for (FireStation fireStation:firestations) {
            String currentAdress = fireStation.getAddress();
            String fullName = currentAdress.toLowerCase().replaceAll("\\s","");
            if (toCompare.equals(fullName)){
                selectedFirestation = firestations.get(index);
            }
            index++;
        }
        return selectedFirestation;
    }
}
