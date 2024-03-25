package com.safetynet.alerts;

import com.safetynet.alerts.model.JsonElts;
import com.safetynet.alerts.services.JsonReader;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

@SpringBootTest
public class JsonReadTests {

    @Autowired
    private JsonReader jsonReader;

    public JsonElts sharedJson() throws IOException {
        return jsonReader.getJson();
    }
    @Test
    public void isJsonRead() throws IOException, ParseException {
        JsonElts jsonElts = sharedJson();
        assertNotNull(jsonElts);
        assertNotNull(jsonElts.getFirestations());
        assertNotNull(jsonElts.getPersons());
        assertNotNull(jsonElts.getMedicalrecords());
    }
}
