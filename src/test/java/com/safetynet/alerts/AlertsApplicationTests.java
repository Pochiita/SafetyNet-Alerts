package com.safetynet.alerts;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.controller.TestController;
import com.safetynet.alerts.model.*;
import com.safetynet.alerts.controller.CrudController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.List;

@WebMvcTest(controllers = CrudController.class)
@Import(CrudController.class)
public class AlertsApplicationTests {

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
	private CrudController crudController;

	private ListSearcher listSearcher;

	@BeforeEach
	public void setUp() throws IOException {
		JsonElts jsonElts = setJsonElts();
		when(jsonReader.getJson()).thenReturn(jsonElts);
	}

	/**
	 *PERSONS
	 */

	@Test
	public void canDeletePerson() throws Exception {
		JsonElts jsonElts = setJsonElts();
		int personsListSize = jsonElts.getPersons().size();
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.delete("/person/johnboyd"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		assertEquals(jsonElts.getPersons().size(),personsListSize-1);
	}

	@Test
	public void cantDeletePerson()throws Exception{
		JsonElts jsonElts = setJsonElts();
		int personsListSize = jsonElts.getPersons().size();
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.delete("/person/johnbody"))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
		assertEquals(jsonElts.getPersons().size(),personsListSize);
	}

 	@Test
	public void canPutAPerson()throws Exception{
		listSearcher = new ListSearcher();
		JsonElts jsonElts = setJsonElts();
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.put("/person/johnboyd")
						.param("city","test")
						.param("address","test")
						.param("zip","test")
						.param("phone","test")
						.param("mail","test"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		Person person = listSearcher.searchAPersonInAList("johnboyd",jsonElts.getPersons());
		assertEquals(person.getCity(),"test");
		assertEquals(person.getAddress(),"test");
		assertEquals(person.getZip(),"test");
		assertEquals(person.getPhone(),"test");
		assertEquals(person.getEmail(),"test");
	}

	@Test
	public void InexistantPutPerson()throws Exception{
		listSearcher = new ListSearcher();
		JsonElts jsonElts = setJsonElts();
		Person person = listSearcher.searchAPersonInAList("johnbody",jsonElts.getPersons());
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.put("/person/johnbody")
						.param("city","test")
						.param("address","test")
						.param("zip","test")
						.param("phone","test")
						.param("mail","test"))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
		assertNull(person);
	}

	@Test
	public void createAPerson()throws Exception{
		listSearcher = new ListSearcher();
		JsonElts jsonElts = setJsonElts();
		int listSize = jsonElts.getPersons().size();
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.post("/person/")
						.param("firstName","test")
						.param("lastName","test")
						.param("city","test")
						.param("address","test")
						.param("zip","test")
						.param("phone","test")
						.param("mail","test"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		Person person = listSearcher.searchAPersonInAList("testtest",jsonElts.getPersons());

		assertEquals(listSize+1,jsonElts.getPersons().size());
		assertNotNull(person);
	}

	@Test
	public void cantCreateAPerson()throws Exception{
		listSearcher = new ListSearcher();
		JsonElts jsonElts = setJsonElts();
		int listSize = jsonElts.getPersons().size();
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.post("/person/")
						.param("firstName","test")
						.param("city","test")
						.param("address","test")
						.param("zip","test")
						.param("phone","test")
						.param("mail","test"))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
		Person person = listSearcher.searchAPersonInAList("testtest",jsonElts.getPersons());
		assertEquals(listSize,jsonElts.getPersons().size());
		assertNull(person);
	}

	/**
	 * Firestations
	 */

	@Test
	public void createAFireStation()throws Exception{
		listSearcher = new ListSearcher();
		JsonElts jsonElts = setJsonElts();
		int listSize = jsonElts.getFirestations().size();
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.post("/firestation/")
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
		JsonElts jsonElts = setJsonElts();
		int listSize = jsonElts.getFirestations().size();
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.post("/firestation/")
						.param("address","test"))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
		assertEquals(listSize,jsonElts.getFirestations().size());
		FireStation fireStation = listSearcher.searchAFireStationByAddress("test",jsonElts.getFirestations());
		assertNull(fireStation);

	}

	@Test
	public void canPutAFirestation()throws Exception{
		listSearcher = new ListSearcher();
		JsonElts jsonElts = setJsonElts();
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.put("/firestation/90873rdst")
						.param("station","1"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		FireStation fireStation = listSearcher.searchAFireStationByAddress("90873rdst",jsonElts.getFirestations());
		assertEquals(fireStation.getStation(),"1");
	}

	@Test
	public void InexistantPutFirestation()throws Exception{
		listSearcher = new ListSearcher();
		JsonElts jsonElts = setJsonElts();
		FireStation fireStation = listSearcher.searchAFireStationByAddress("3rdst",jsonElts.getFirestations());
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.put("/firestation/3rdst"))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
		assertNull(fireStation);
	}

	@Test
	public void canDeleteFireStationByStation ()throws Exception{
		listSearcher = new ListSearcher();
		JsonElts jsonElts = setJsonElts();
		List<FireStation> firestations = listSearcher.searchAFireStationByValue("station","1",jsonElts.getFirestations());
		assertNotNull(firestations);
		System.out.println(jsonElts.getFirestations().size());
		int toCompareListSize = jsonElts.getFirestations().size()-firestations.size();
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
						.param("queryby","station")
						.param("option","1"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		assertEquals(jsonElts.getFirestations().size(),toCompareListSize);
	}

	@Test
	public void canDeleteFireStationByAddress()throws Exception{
		listSearcher = new ListSearcher();
		JsonElts jsonElts = setJsonElts();
		List<FireStation> firestations = listSearcher.searchAFireStationByValue("address","1509 Culver St",jsonElts.getFirestations());
		assertNotNull(firestations);
		int toCompareListSize = jsonElts.getFirestations().size()-firestations.size();
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
						.param("queryby","address")
						.param("option","1509 Culver St"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		assertEquals(jsonElts.getFirestations().size(),toCompareListSize);
	}

	@Test
	public void cantDeleteFireStationBadParamAddress()throws Exception{
		listSearcher = new ListSearcher();
		JsonElts jsonElts = setJsonElts();
		List<FireStation> firestations = listSearcher.searchAFireStationByValue("adres","1509 Culver St",jsonElts.getFirestations());
		assertEquals(firestations.size(),0);
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
						.param("queryby","addres")
						.param("option","1509 Culver St"))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	public void cantDeleteFireStationNotDigitsOnlyStation()throws Exception{
		listSearcher = new ListSearcher();
		JsonElts jsonElts = setJsonElts();
		List<FireStation> firestations = listSearcher.searchAFireStationByValue("station","1a",jsonElts.getFirestations());
		assertEquals(firestations.size(),0);
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
						.param("queryby","station")
						.param("option","1a"))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	public void InexistantFireStationToDelete()throws Exception{
		listSearcher = new ListSearcher();
		JsonElts jsonElts = setJsonElts();
		List<FireStation> fireStations = listSearcher.searchAFireStationByValue("address","rue des coquelicots",jsonElts.getFirestations());
		assertEquals(fireStations.size(),0);
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
						.param("queryby","address")
						.param("option","rue des coquelicots"))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());

	}

}