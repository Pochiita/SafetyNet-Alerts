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
    public ResponseEntity<String> createMedicalRecord (@RequestParam(value ="firstName") String firstName, @RequestParam(value ="lastName") String lastName, @RequestParam(value ="birthdate") String birthDate, @RequestParam(value="medication") List<String> medication, @RequestParam(value="allergies") List<String> allergies) throws IOException {
        return medicalRecordServices.createMedicalRecord(firstName, lastName, birthDate, medication, allergies);
    }

    @DeleteMapping("/medicalrecords/{name}")
    public ResponseEntity<String> deleteMedicalRecord(@PathVariable String name) throws IOException {
        logger.info(urlLogger());

        List<MedicalRecord> medicalRecordList = sharedJson().getMedicalrecords();
        String toCompare = name.toLowerCase();
        int index = 0;
        boolean canDelete = false;
        for (MedicalRecord medicalRecordSingle:medicalRecordList) {
            String firstName = medicalRecordSingle.getFirstName();
            String lastName = medicalRecordSingle.getLastName();
            String fullName = firstName.concat(lastName).toLowerCase();
            if (toCompare.equals(fullName)){
                canDelete = true;
                break;
            }

            index++;
        }
        if (canDelete) {
            medicalRecordList.remove(index);
            sharedJson().setMedicalrecords(medicalRecordList);
            String msg = " 'MedicalRecord' correctly deleted - Value:".concat(name);
            logger.info(msg);

        }else{
            String msg = " 'MedicalRecord' couldn't be deleted - Not found by value :".concat(name);
            logger.error(msg);
            return new ResponseEntity<>("Not deleted", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Correctly deleted",HttpStatus.OK);
    }

    @PutMapping("/medicalrecord/{name}")
    public ResponseEntity<String> modifyMedicalRecord (@PathVariable String name,@RequestParam(value ="birthdate") String birthDate,@RequestParam(value="medication") List<String> medication,@RequestParam(value="allergies") List<String> allergies) throws IOException {
        logger.info(urlLogger());

        List<MedicalRecord> medicalRecordList = sharedJson().getMedicalrecords();
        MedicalRecord selectedMedicalRecord = listSearcher.searchAMedicalRecordInAList(name,medicalRecordList);
        if (selectedMedicalRecord != null) {
            selectedMedicalRecord.setBirthDate(birthDate);
            selectedMedicalRecord.setMedications(medication);
            selectedMedicalRecord.setAllergies(allergies);
            String msg = " 'MedicalRecord' correctly modified - Value:".concat(name);
            logger.info(msg);
            return new ResponseEntity<>("Correctly modified", HttpStatus.OK);

        }else{
            String msg = " 'Person' couldn't be modified - Not found by value :".concat(name);
            logger.error(msg);
            return new ResponseEntity<>("Not modified", HttpStatus.BAD_REQUEST);
        }
    }


}
