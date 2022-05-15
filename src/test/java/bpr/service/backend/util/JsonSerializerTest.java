package bpr.service.backend.util;

import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.models.entities.UuidEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonSerializerTest {

    private ISerializer serializer;

    @BeforeEach
    public void beforeAll() {
        serializer = new JsonSerializer();
    }

    @Test
    public void canCreateJsonNode() {
        //arrange
        String actual = "{\"id\":\"1\",\"uuids\":[{\"id\":null,\"uuid\":\"Uuuid1\"}],\"tags\":[{\"id\":null,\"tag\":\"tag1\"}]}";

        // act
        var result = serializer.getJsonNode(actual);

        // assert
        assertEquals(actual, result.toString());
    }

    @Test
    public void canCreateJson() throws JsonProcessingException {
        // arrange
        TestClass t = new TestClass(1L, "testField");
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(t);

        // act
        String test = serializer.toJson(t);

        // assert
        assertEquals(expected, test);
    }


    private static class TestClass {
        public Long id;
        public String testField;

        public TestClass(Long id, String testField) {
            this.id = id;
            this.testField = testField;
        }
    }
}