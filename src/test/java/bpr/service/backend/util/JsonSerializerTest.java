package bpr.service.backend.util;

import bpr.service.backend.MqttMessage;
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
        MqttMessage message = new MqttMessage("This is a simple test");
        String expectedOutput = "{\"rawMessage\":\"This is a simple test\"}";


        // act
        var actual = serializer.toJson(message);

        // assert
        assertEquals(expectedOutput, actual);
    }
}