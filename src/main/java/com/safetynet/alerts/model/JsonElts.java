package com.safetynet.alerts.model;

import java.util.List;

public class JsonElts {

    private List persons;

    private List firestations;

    private List medicalrecords;

    public List getPersons() {
        return persons;
    }
    public List getFirestations() {
        return firestations;
    }

    public List getMedicalrecords() {
        return medicalrecords;
    }

    public void setPersons(List persons) {
        this.persons = persons;
    }

    public void setFirestations(List firestations) {
        this.firestations = firestations;
    }

    public void setMedicalrecords(List medicalrecords) {
        this.medicalrecords = medicalrecords;
    }
}
