package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.services.JsonReader;
import com.safetynet.alerts.services.ListSearcher;
import com.safetynet.alerts.services.MedicalRecordServices;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping
public class MedicalRecordController {

    @Autowired
    private JsonReader jsonReader;

    @Autowired
    private MedicalRecordServices medicalRecordServices;

    Logger logger = LoggerFactory.getLogger(JsonElts.class);

    ListSearcher listSearcher = new ListSearcher();

    public JsonElts sharedJson() throws IOException {
        return jsonReader.getJson();
    }

    public String urlLogger() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String url = request.getRequestURL().toString();
        String ipAddress = request.getRemoteAddr();
        return "The URL used to access this route is: " + url + " and the IP address is: " + ipAddress;
    }

    @PostMapping("/medicalrecords")
    public ResponseEntity<String> createMedicalRecord(@RequestParam(value = "firstName") String firstName, @RequestParam(value = "lastName") String lastName, @RequestParam(value = "birthdate") String birthDate, @RequestParam(value = "medication") List<String> medication, @RequestParam(value = "allergies") List<String> allergies) throws IOException {
        ResponseEntity<String> response =  medicalRecordServices.createMedicalRecord(firstName, lastName, birthDate, medication, allergies);

        if (response.getStatusCode().is2xxSuccessful()){
            String msg = " 'MedicalRecord' correctly created";
            logger.info(msg);
        }else{
            String msg = " 'MedicalRecord' couldn't be created";
            logger.error(msg);
        }

        return response;
    }

    @DeleteMapping("/medicalrecords/{name}")
    public ResponseEntity<String> deleteMedicalRecord(@PathVariable String name) throws IOException {
        ResponseEntity<String> response = medicalRecordServices.deleteMedicalRecord(name);

        if (response.getStatusCode().is2xxSuccessful()){
            String msg = " 'MedicalRecord' correctly modified";
            logger.info(msg);
        }else{
            String msg = " 'MedicalRecord' couldn't be modified";
            logger.error(msg);
        }
        return response;
    }

    @PutMapping("/medicalrecords/{name}")
    public ResponseEntity<String> modifyMedicalRecord(@PathVariable String name, @RequestParam(value = "birthdate") String birthDate, @RequestParam(value = "medication") List<String> medication, @RequestParam(value = "allergies") List<String> allergies) throws IOException {
        ResponseEntity<String> response = medicalRecordServices.modifyMedicalRecord(name, birthDate, medication, allergies);

        if (response.getStatusCode().is2xxSuccessful()){
            String msg = " 'MedicalRecord' correctly deleted";
            logger.info(msg);
        }else{
            String msg = " 'MedicalRecord' couldn't be deleted";
            logger.error(msg);
        }
        return response;
    }


}
