package com.safetynet.alerts.services;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListSearcher {


    public Person searchAPersonInAList (String name, List<Person> list){
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

    public List<Person> searchPersonsInAListByAddress(String address, List<Person> list){
        List<Person> persons = list;
        String toCompare = address.toLowerCase().replaceAll("\\s", "");
        int index = 0;
        List<Person> listSelectedPersons =new ArrayList<Person>();
        for (Person person:persons) {
            String addressPerson = person.getAddress().toLowerCase().replaceAll("\\s", "");

            if (toCompare.equals(addressPerson)){
                listSelectedPersons.add(persons.get(index));
            }
            index++;
        }
        return listSelectedPersons;
    }

    public List<Person> searchPersonsInAListByCity(String city, List<Person> list){
        List<Person> persons = list;
        String toCompare = city.toLowerCase().replaceAll("\\s", "");
        int index = 0;
        List<Person> listSelectedPersons =new ArrayList<Person>();
        for (Person person:persons) {
            String cityPerson = person.getCity().toLowerCase().replaceAll("\\s", "");

            if (toCompare.equals(cityPerson)){
                listSelectedPersons.add(persons.get(index));
            }
            index++;
        }
        return listSelectedPersons;
    }

    public FireStation searchAFireStationByAddress(String address, List<FireStation> list){
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

    public FireStation searchAFireStationByStation(String stationNumber, List<FireStation> list){
        List<FireStation> firestations = list;
        String toCompare = stationNumber.replaceAll("\\s","");
        int index = 0;
        FireStation selectedFirestation = null;
        for (FireStation fireStation:firestations) {
            String currentStationNumber = fireStation.getStation().replaceAll("\\s","");
            if (toCompare.equals(currentStationNumber)){
                selectedFirestation = firestations.get(index);
            }
            index++;
        }
        return selectedFirestation;
    }

    public List<FireStation> searchAFireStationByValue(String option,String value,List<FireStation> list){
        List<FireStation> firestations = list;
        String baseValue = value;
        String toCompare = "";
        if(Objects.equals(option, "address")){
            toCompare = baseValue.toLowerCase().replaceAll("\\s","");
        }else if(Objects.equals(option, "station")){
            toCompare = baseValue;
        }
        int index = 0;
        List<FireStation> selectedFirestations = new ArrayList<FireStation>();
        for (FireStation fireStation:firestations) {
            if(Objects.equals(option, "address")){
                String currentAdress = fireStation.getAddress().toLowerCase().replaceAll("\\s","");
                if (toCompare.equals(currentAdress)){
                    selectedFirestations.add(firestations.get(index));
                }
                index++;
            }else if(Objects.equals(option, "station")){
                String currentStation = fireStation.getStation();
                if (toCompare.equals(currentStation)){
                    selectedFirestations.add(firestations.get(index));
                }
                index++;
            }
        }
        return selectedFirestations;
    }

    public MedicalRecord searchAMedicalRecordInAList(String name, List<MedicalRecord> list){
        List<MedicalRecord> medicalRecordList = list;
        String toCompare = name.toLowerCase();
        int index = 0;
        MedicalRecord selectedMedicalRecord = null;
        for (MedicalRecord medicalRecord:medicalRecordList) {
            String firstName = medicalRecord.getFirstName();
            String lastName = medicalRecord.getLastName();
            String fullName = firstName.concat(lastName).toLowerCase();
            if (toCompare.equals(fullName)){
                selectedMedicalRecord = medicalRecordList.get(index);
            }
            index++;
        }
        return selectedMedicalRecord;
    }
}
