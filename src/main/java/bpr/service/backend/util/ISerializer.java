package bpr.service.backend.util;

import bpr.service.backend.models.entities.CustomerEntity;
import com.fasterxml.jackson.databind.JsonNode;

public interface ISerializer {
    JsonNode getJsonNode(String json);
}
