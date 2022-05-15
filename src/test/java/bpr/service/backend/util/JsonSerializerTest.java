package bpr.service.backend.util;

import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.models.entities.UuidEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    public void toJsonSunny() {
        //arrange
        CustomerEntity customerEntity = new CustomerEntity(List.of(new UuidEntity("Uuuid1")), List.of(new TagEntity("tag1")));
        String expectedOutput = "{\"id\":null,\"uuids\":[{\"id\":null,\"uuid\":\"Uuuid1\"}],\"tags\":[{\"id\":null,\"tag\":\"tag1\"}]}";

        // act
        var actual = serializer.toJson(customerEntity);

        // assert
        assertEquals(expectedOutput, actual);
    }
}