package com.safetynet.alerts;
import com.safetynet.alerts.controller.TestController;
import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.model.JsonReader;
import com.safetynet.alerts.controller.CrudController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.Mockito.when;
import java.io.IOException;

@WebMvcTest(controllers = TestController.class)
@Import(TestController.class)
public class AlertsApplicationTests {

	@Autowired
	@MockBean
	private JsonReader jsonReader;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testYourEndpoint() throws Exception {
		JsonElts jsonElts = new JsonElts();
		when(jsonReader.getJson()).thenReturn(jsonElts);
		mockMvc.perform(MockMvcRequestBuilders.get("/test/person"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
}