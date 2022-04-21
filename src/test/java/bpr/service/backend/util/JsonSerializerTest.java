package bpr.service.backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonSerializerTest {

    private ISerializer serializer;

    @BeforeEach
    public void beforeAll() {
        serializer = new JsonSerializer();
    }

    @Test
    public void toJsonSunny() throws JSONException, JsonProcessingException {

        // arrange
        Map<String, Object> details = new HashMap<>();
        details.put("id", 1);
        details.put("testStr", "String");
        String expectedOutput = "{\"testStr\":\"String\",\"id\":1}";


        // act
        var actual = serializer.toJson(details);

        // assert
        assertEquals(expectedOutput, actual);
    }
}