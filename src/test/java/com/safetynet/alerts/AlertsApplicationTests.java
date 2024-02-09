package com.safetynet.alerts;
import com.safetynet.alerts.controller.CrudController;
import com.safetynet.alerts.model.JsonReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
@ExtendWith(MockitoExtension.class)
@WebMvcTest(CrudController.class)
class AlertsApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@Mock
	private JsonReader jsonReader;

	@InjectMocks
	private CrudController crudController;

	@Test
	void contextLoads() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/person/{name}","johnboyd"))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

}
