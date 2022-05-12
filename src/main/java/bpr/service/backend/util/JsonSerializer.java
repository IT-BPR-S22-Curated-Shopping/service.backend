package bpr.service.backend.util;

import bpr.service.backend.models.entities.CustomerEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("JsonSerializer")
public class JsonSerializer implements ISerializer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public String toJson(CustomerEntity payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            logger.error("Problem Serializing payload: " + payload + ", with error: " + e.getMessage());
        }
        return null;
    }

    public CustomerEntity fromJsonToCustomer(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, CustomerEntity.class);
        } catch (JsonProcessingException e) {
            logger.error("Problem deserializing json: " + e.getMessage());
        }
        return null;
    }

    @Override
    public JsonNode getJsonNode(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
