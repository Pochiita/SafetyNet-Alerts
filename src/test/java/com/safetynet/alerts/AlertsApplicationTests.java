package com.safetynet.alerts;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.*;
import com.safetynet.alerts.services.JsonReader;
import com.safetynet.alerts.services.ListSearcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebMvcTest()
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

	/**
	 * MecialRecords
	 */

	@Test
	public void canDeleteMedicalRecord() throws Exception {
		JsonElts jsonElts = setJsonElts();
		int personsListSize = jsonElts.getMedicalrecords().size();
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.delete("/medicalrecords/johnboyd"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		assertEquals(jsonElts.getMedicalrecords().size(),personsListSize-1);
	}

	@Test
	public void cantDeleteMedicalRecord()throws Exception{
		JsonElts jsonElts = setJsonElts();
		int personsListSize = jsonElts.getMedicalrecords().size();
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.delete("/medicalrecords/johnbody"))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
		assertEquals(jsonElts.getMedicalrecords().size(),personsListSize);
	}

	@Test
	public void createAMedicalRecord()throws Exception{
		listSearcher = new ListSearcher();
		JsonElts jsonElts = setJsonElts();
		int listSize = jsonElts.getMedicalrecords().size();
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.post("/medicalrecords")
						.param("firstName","test")
						.param("lastName","test")
						.param("birthdate","13/13/2023")
						.param("allergies","test,test")
						.param("medication","test,testr"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		MedicalRecord medicalRecord = listSearcher.searchAMedicalRecordInAList("testtest",jsonElts.getMedicalrecords());

		assertEquals(listSize+1,jsonElts.getMedicalrecords().size());
		assertNotNull(medicalRecord);
	}

	@Test
	public void cantCreateAMedicalRecord()throws Exception{
		listSearcher = new ListSearcher();
		JsonElts jsonElts = setJsonElts();
		int listSize = jsonElts.getMedicalrecords().size();
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.post("/medicalrecord")
						.param("firstName","test")
						.param("lastName","test")
						.param("allergies","test,test")
						.param("medication","test,testr"))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
		MedicalRecord medicalRecord = listSearcher.searchAMedicalRecordInAList("testtest",jsonElts.getMedicalrecords());
		assertEquals(listSize,jsonElts.getMedicalrecords().size());
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
	public void canPutAMedicalRecord()throws Exception{
		listSearcher = new ListSearcher();
		JsonElts jsonElts = setJsonElts();
		List<String> medication = Arrays.asList("Med1", "Med2");
		List<String> allergies = Arrays.asList("Allergy1", "Allergy2");
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.put("/medicalrecord/johnboyd")
						.param("birthdate","13/13/2023")
						.param("allergies",allergies.get(0),allergies.get(1))
						.param("medication",medication.get(0),medication.get(1)))
				.andExpect(MockMvcResultMatchers.status().isOk());
		MedicalRecord medicalRecord = listSearcher.searchAMedicalRecordInAList("johnboyd",jsonElts.getMedicalrecords());
		assertEquals(medicalRecord.getBirthdate(),"13/13/2023");
		assertEquals(medicalRecord.getAllergies().get(0),"Allergy1");
		assertEquals(medicalRecord.getMedications().get(0),"Med1");
	}

	@Test
	public void InexistantPutMedicalRecord()throws Exception{
		listSearcher = new ListSearcher();
		JsonElts jsonElts = setJsonElts();
		MedicalRecord medicalRecord = listSearcher.searchAMedicalRecordInAList("johnbody",jsonElts.getMedicalrecords());
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.post("/medicalrecord")
						.param("birthdate","13/13/2023")
						.param("allergies","test,test")
						.param("medication","test,testr"))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
		assertNull(medicalRecord);
	}







}