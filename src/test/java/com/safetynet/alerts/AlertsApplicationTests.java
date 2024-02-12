package com.safetynet.alerts;
import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.model.JsonReader;
import com.safetynet.alerts.controller.CrudController;
import com.safetynet.alerts.model.Person;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = CrudController.class)
@Import(CrudController.class)
public class AlertsApplicationTests {

	@Autowired
	@MockBean
	private JsonReader jsonReader;

	private JsonElts jsonElts;

	@Autowired
	private MockMvc mockMvc;

	@InjectMocks
	CrudController crudController;


	public JsonElts sharedJson() throws IOException {
		return jsonReader.getJson();
	}

	@Test
	public void testYourEndpoint() throws Exception {
		JsonElts jsonElts = new JsonElts();
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.delete("/person/johnboyd"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
}