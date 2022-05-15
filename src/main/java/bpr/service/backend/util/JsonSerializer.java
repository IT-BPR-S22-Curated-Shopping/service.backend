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
