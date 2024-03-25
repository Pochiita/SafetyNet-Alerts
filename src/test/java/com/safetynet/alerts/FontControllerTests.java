package com.safetynet.alerts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.controller.FrontController;
import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.services.JsonReader;
import com.safetynet.alerts.services.ListSearcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest()
public class FontControllerTests {


    @Autowired
    @MockBean
    private JsonReader jsonReader;

    public JsonElts setJsonElts() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File("src/main/resources/data.json"), JsonElts.class);
    }

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private FrontController frontController;

    private ListSearcher listSearcher;

    @BeforeEach
    public void setUp() throws IOException {
        JsonElts jsonElts = setJsonElts();
        when(jsonReader.getJson()).thenReturn(jsonElts);
    }

    @Test
    public void testPeopleConcernedByStation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/firestation")
                        .param("stationNumber", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testPeopleConcernedByInexistantStation() throws Exception {
        String inexistantFireStation = "999999";
        String json = "{\n" +
                "\t\"personsStation\": [],\n" +
                "\t\"totalAdultsNumber\": -1,\n" +
                "\t\"totalChildrenNumber\": -1\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.get("/firestation")
                        .param("stationNumber", inexistantFireStation)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(json));

    }

    @Test
    public void testChildAlert() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/childAlert")
                        .param("address", "1509 Culver St")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testPeopleConcernedByInexistantAddress() throws Exception {
        String inexistantAddress = "marmande";
        String json = "{\n" +
                "\t\"minorsList\": [],\n" +
                "\t\"adultsInSameHouse\": []\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.get("/childAlert")
                        .param("address", inexistantAddress)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(json));

    }

    @Test
    public void testPeopleConcernedByInexistantMinors() throws Exception {
        String inexistantChild = "644 Gershwin Cir";
        String json = "{\n" +
                "\t\"minorsList\": [],\n" +
                "\t\"adultsInSameHouse\": []\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.get("/childAlert")
                        .param("address", inexistantChild)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(json));

    }

    @Test
    public void testPhoneAlert()throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/phoneAlert")
                        .param("firestation", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testPhoneAlertInexistantFirestation()throws Exception{
        List<HashMap> returningData = new ArrayList<>();
        String json = "{\n" +
                "\t\"phoneNumber\": []\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.get("/phoneAlert")
                        .param("firestation", "789789798")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(json));

    }

    @Test
    public void testFloodAlert()throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/flood/stations")
                        .param("stations", "1,2,3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void testFloodAlertInexistantStation()throws Exception {
        HashMap<String,List<HashMap>> addressDisplayer = new HashMap<>();

        mockMvc.perform(MockMvcRequestBuilders.get("/flood/stations")
                        .param("stations", "acf,7fd8,zae")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(addressDisplayer.toString()));

    }

    @Test
    public void testFireAlert()throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/fire")
                        .param("address", "1509 Culver St")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void testFireAlertInexistantAddress()throws Exception {
        List<HashMap> returningData = new ArrayList<>();

        String json = "{\n" +
                "\t\"citizens\": [],\n" +
                "\t\"nbrOfFireStation\": \"-1\"\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.get("/fire")
                        .param("address", "abcdef")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(json));

    }

    @Test
    public void testCommunityEmail()throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/communityEmail")
                        .param("city", "Culver")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }
    @Test
    public void testCommunityEmailInexistantCity()throws Exception {
        List<String> returningData = new ArrayList<>();
        String json = "{\"mails\":[]}";
        mockMvc.perform(MockMvcRequestBuilders.get("/communityEmail")
                        .param("city", "abcdef")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(json));

    }






}
