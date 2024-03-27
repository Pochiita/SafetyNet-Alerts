package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.services.JsonReader;
import com.safetynet.alerts.services.ListSearcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebMvcTest
public class MedicalRecordsControllerTests {

    @Autowired
    private JsonReader jsonReader;

    @Autowired
    private MockMvc mockMvc;

    private ListSearcher listSearcher;

    @Test
    public void canDeleteMedicalRecord() throws Exception {
        JsonElts jsonElts = jsonReader.getJson();
        int personsListSize = jsonElts.getMedicalrecords().size();
        mockMvc.perform(MockMvcRequestBuilders.delete("/medicalrecords/johnboyd"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertEquals(jsonElts.getMedicalrecords().size(), personsListSize - 1);
    }

    @Test
    public void cantDeleteMedicalRecord() throws Exception {
        JsonElts jsonElts = jsonReader.getJson();
        int personsListSize = jsonElts.getMedicalrecords().size();
        mockMvc.perform(MockMvcRequestBuilders.delete("/medicalrecords/johnbody"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        assertEquals(jsonElts.getMedicalrecords().size(), personsListSize);
    }

    @Test
    public void createAMedicalRecord() throws Exception {
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        int listSize = jsonElts.getMedicalrecords().size();
        mockMvc.perform(MockMvcRequestBuilders.post("/medicalrecords")
                        .param("firstName", "test")
                        .param("lastName", "test")
                        .param("birthdate", "13/13/2023")
                        .param("allergies", "test,test")
                        .param("medication", "test,test"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        MedicalRecord medicalRecord = listSearcher.searchAMedicalRecordInAList("testtest", jsonElts.getMedicalrecords());

        assertEquals(listSize + 1, jsonElts.getMedicalrecords().size());
        assertNotNull(medicalRecord);
    }

    @Test
    public void cantCreateAMedicalRecord() throws Exception {
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        int listSize = jsonElts.getMedicalrecords().size();
        mockMvc.perform(MockMvcRequestBuilders.post("/medicalrecord")
                        .param("firstName", "invalid")
                        .param("lastName", "person")
                        .param("birthdate", "13/13/azeaze")
                        .param("allergies", "test,test")
                        .param("medication", "test,testr"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        MedicalRecord medicalRecord = listSearcher.searchAMedicalRecordInAList("invalidperson", jsonElts.getMedicalrecords());
        assertEquals(listSize, jsonElts.getMedicalrecords().size());
        assertNull(medicalRecord);
    }


    @Test
    public void testCreateMedicalRecordInvalidBirthDate() throws Exception {
        String firstName = "John";
        String lastName = "Doe";
        String birthDate = "01/abc/1990";
        List<String> medication = Arrays.asList("Med1", "Med2");
        List<String> allergies = Arrays.asList("Allergy1", "Allergy2");

        mockMvc.perform(MockMvcRequestBuilders.post("/medicalrecords")
                .param("firstName", firstName)
                .param("lastName", lastName)
                .param("birthdate", birthDate)
                .param("medication", medication.get(0), medication.get(1))
                .param("allergies", allergies.get(0), allergies.get(1))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    public void canPutAMedicalRecord() throws Exception {
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        List<String> medication = Arrays.asList("Med1", "Med2");
        List<String> allergies = Arrays.asList("Allergy1", "Allergy2");
        mockMvc.perform(MockMvcRequestBuilders.put("/medicalrecords/testtest")
                        .param("birthdate", "13/13/2023")
                        .param("allergies", allergies.get(0), allergies.get(1))
                        .param("medication", medication.get(0), medication.get(1)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        MedicalRecord medicalRecord = listSearcher.searchAMedicalRecordInAList("testtest", jsonElts.getMedicalrecords());
        assertEquals(medicalRecord.getBirthdate(), "13/13/2023");
        assertEquals(medicalRecord.getAllergies().get(0), "Allergy1");
        assertEquals(medicalRecord.getMedications().get(0), "Med1");
    }

    @Test
    public void InexistantPutMedicalRecord() throws Exception {
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        MedicalRecord medicalRecord = listSearcher.searchAMedicalRecordInAList("johnbody", jsonElts.getMedicalrecords());
        mockMvc.perform(MockMvcRequestBuilders.post("/medicalrecords")
                        .param("birthdate", "13/13/2023")
                        .param("allergies", "test,test")
                        .param("medication", "test,testr"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        assertNull(medicalRecord);
    }

}
