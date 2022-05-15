package bpr.service.backend.util;

import bpr.service.backend.models.entities.CustomerEntity;
import com.fasterxml.jackson.databind.JsonNode;

public interface ISerializer {
    String toJson(Object toJson);
    JsonNode getJsonNode(String json);
}
