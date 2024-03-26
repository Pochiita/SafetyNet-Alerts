package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.model.Person;
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
import static org.mockito.Mockito.when;
@WebMvcTest
public class PersonsControllerTests {

    @Autowired
    private JsonReader jsonReader;

    @Autowired
    private MockMvc mockMvc;

    private ListSearcher listSearcher;

    @Test
    public void canPutAPerson() throws Exception {
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        mockMvc.perform(MockMvcRequestBuilders.put("/person/TenleyBoyd")
                        .param("city", "test")
                        .param("address", "test")
                        .param("zip", "test")
                        .param("phone", "test")
                        .param("mail", "test"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Person person = listSearcher.searchAPersonInAList("tenleyboyd", jsonElts.getPersons());
        assertEquals(person.getCity(), "test");
        assertEquals(person.getAddress(), "test");
        assertEquals(person.getZip(), "test");
        assertEquals(person.getPhone(), "test");
        assertEquals(person.getEmail(), "test");
    }

    @Test
    public void InexistantPutPerson() throws Exception {
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        Person person = listSearcher.searchAPersonInAList("johnbody", jsonElts.getPersons());
        mockMvc.perform(MockMvcRequestBuilders.put("/person/johnbody")
                        .param("city", "test")
                        .param("address", "test")
                        .param("zip", "test")
                        .param("phone", "test")
                        .param("mail", "test"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        assertNull(person);
    }

    @Test
    public void canDeletePerson() throws Exception {
        JsonElts jsonElts = jsonReader.getJson();
        int personsListSize = jsonElts.getPersons().size();
        mockMvc.perform(MockMvcRequestBuilders.delete("/person/johnboyd"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertEquals(jsonElts.getPersons().size(), personsListSize - 1);
    }

    @Test
    public void cantDeletePerson() throws Exception {
        JsonElts jsonElts = jsonReader.getJson();
        int personsListSize = jsonElts.getPersons().size();
        mockMvc.perform(MockMvcRequestBuilders.delete("/person/johnbody"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        assertEquals(jsonElts.getPersons().size(), personsListSize);
    }


    @Test
    public void createAPerson() throws Exception {
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        int listSize = jsonElts.getPersons().size();
        mockMvc.perform(MockMvcRequestBuilders.post("/person")
                        .param("firstName", "test")
                        .param("lastName", "test")
                        .param("city", "test")
                        .param("address", "test")
                        .param("zip", "test")
                        .param("phone", "test")
                        .param("mail", "test"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Person person = listSearcher.searchAPersonInAList("testtest", jsonElts.getPersons());

        assertEquals(listSize + 1, jsonElts.getPersons().size());
        assertNotNull(person);
    }

    @Test
    public void cantCreateAPerson() throws Exception {
        listSearcher = new ListSearcher();
        JsonElts jsonElts = jsonReader.getJson();
        int listSize = jsonElts.getPersons().size();
        mockMvc.perform(MockMvcRequestBuilders.post("/person/")
                        .param("firstName", "test")
                        .param("city", "test")
                        .param("address", "test")
                        .param("zip", "test")
                        .param("phone", "test")
                        .param("mail", "test"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        Person person = listSearcher.searchAPersonInAList("testtest", jsonElts.getPersons());
        assertEquals(listSize, jsonElts.getPersons().size());
        assertNull(person);
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
    public void testPhoneAlert() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/phoneAlert")
                        .param("firestation", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testPhoneAlertInexistantFirestation() throws Exception {
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

    @Test
    public void testPersonInfo()throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/personinfo")
            .param("firstName", "Zach")
            .param("lastName","Zemicks")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testInexistantPersonInfo()throws Exception{
        String json = "{\n" + "\t\"persons\": []\n" + "}";
        mockMvc.perform(MockMvcRequestBuilders.get("/personinfo")
                        .param("firstName", "Je")
                        .param("lastName","Nexistepas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(json));
    }
}