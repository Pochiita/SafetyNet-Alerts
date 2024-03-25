package com.safetynet.alerts.model;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

public class JsonElts {

    private List<Person> persons;

    private List<FireStation> firestations;

    private List<MedicalRecord> medicalrecords;

    public List<Person> getPersons() {
        return persons;
    }
    public List<FireStation> getFirestations() {
        return firestations;
    }

    public List<MedicalRecord> getMedicalrecords() {
        return medicalrecords;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public void setFirestations(List<FireStation> firestations) {
        this.firestations = firestations;
    }

    public void setMedicalrecords(List<MedicalRecord> medicalrecords) {
        this.medicalrecords = medicalrecords;
    }
}
