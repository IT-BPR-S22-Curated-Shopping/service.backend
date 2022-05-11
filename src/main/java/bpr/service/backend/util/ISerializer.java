package bpr.service.backend.util;

import bpr.service.backend.models.DeviceModel;
import bpr.service.backend.models.entities.CustomerEntity;
import com.fasterxml.jackson.databind.JsonNode;

public interface ISerializer {
    String toJson(DeviceModel payload);
    String toJson(CustomerEntity payload);
    DeviceModel fromJsonToDeviceModel(String json);
    CustomerEntity fromJsonToCustomer(String json);
    JsonNode getJsonNode(String json);
}
