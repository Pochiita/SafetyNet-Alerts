package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.services.JsonReader;
import com.safetynet.alerts.services.ListSearcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@WebMvcTest
public class FirestationsControllerTests {

    @Autowired
    private JsonReader jsonReader;

    @Autowired
    private MockMvc mockMvc;

    private ListSearcher listSearcher;
    @Test
    public void createAFireStation()throws Exception{
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        int listSize = jsonElts.getFirestations().size();
        mockMvc.perform(MockMvcRequestBuilders.post("/firestation")
                        .param("address","test")
                        .param("station","test"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        FireStation fireStation = listSearcher.searchAFireStationByAddress("test",jsonElts.getFirestations());

        assertEquals(listSize+1,jsonElts.getFirestations().size());
        assertNotNull(fireStation);
    }

    @Test
    public void cantCreateAFireStation()throws Exception{
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        int listSize = jsonElts.getFirestations().size();
        mockMvc.perform(MockMvcRequestBuilders.post("/firestation")
                        .param("address","test"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        assertEquals(listSize,jsonElts.getFirestations().size());
        FireStation fireStation = listSearcher.searchAFireStationByAddress("test",jsonElts.getFirestations());
        assertNull(fireStation);

    }

    @Test
    public void canPutAFirestation()throws Exception{
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        mockMvc.perform(MockMvcRequestBuilders.put("/firestation/951 LoneTree Rd")
            .param("station","55"))
            .andExpect(MockMvcResultMatchers.status().isOk());
        FireStation fireStation = listSearcher.searchAFireStationByAddress("951 LoneTree Rd",jsonElts.getFirestations());
        assertEquals(fireStation.getStation(),"55");
    }

    @Test
    public void InexistantPutFirestation()throws Exception{
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        FireStation fireStation = listSearcher.searchAFireStationByAddress("3rdst",jsonElts.getFirestations());
        mockMvc.perform(MockMvcRequestBuilders.put("/firestation/3rdst"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        assertNull(fireStation);
    }

    @Test
    public void canDeleteFireStationByStation ()throws Exception{
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        List<FireStation> firestations = listSearcher.searchAFireStationByValue("station","1",jsonElts.getFirestations());
        assertNotNull(firestations);
        System.out.println(jsonElts.getFirestations().size());
        int toCompareListSize = jsonElts.getFirestations().size()-firestations.size();
        mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .param("queryby","station")
                        .param("option","1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertEquals(jsonElts.getFirestations().size(),toCompareListSize);
    }

    @Test
    public void canDeleteFireStationByAddress()throws Exception{
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        List<FireStation> firestations = listSearcher.searchAFireStationByValue("address","1509 Culver St",jsonElts.getFirestations());
        assertNotNull(firestations);
        int toCompareListSize = jsonElts.getFirestations().size()-firestations.size();
        mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .param("queryby","address")
                        .param("option","1509 Culver St"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertEquals(jsonElts.getFirestations().size(),toCompareListSize);
    }

    @Test
    public void cantDeleteFireStationBadParamAddress()throws Exception{
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        List<FireStation> firestations = listSearcher.searchAFireStationByValue("adres","1509 Culver St",jsonElts.getFirestations());
        assertEquals(firestations.size(),0);
        mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .param("queryby","addres")
                        .param("option","1509 Culver St"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void cantDeleteFireStationNotDigitsOnlyStation()throws Exception{
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        List<FireStation> firestations = listSearcher.searchAFireStationByValue("station","1a",jsonElts.getFirestations());
        assertEquals(firestations.size(),0);
        mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .param("queryby","station")
                        .param("option","1a"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void InexistantFireStationToDelete()throws Exception{
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        List<FireStation> fireStations = listSearcher.searchAFireStationByValue("address","rue des coquelicots",jsonElts.getFirestations());
        assertEquals(fireStations.size(),0);
        mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .param("queryby","address")
                        .param("option","rue des coquelicots"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

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


}
