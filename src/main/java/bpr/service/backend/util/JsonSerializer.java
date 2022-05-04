package bpr.service.backend.util;

import bpr.service.backend.models.mqtt.DeviceModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component("JsonSerializer")
public class JsonSerializer implements ISerializer {


    @Override
    public String toJson(DeviceModel payload) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(payload);
    }

    @Override
    public DeviceModel fromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, DeviceModel.class);
    }

}
