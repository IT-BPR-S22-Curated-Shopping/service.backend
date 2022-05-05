package bpr.service.backend.util;

import bpr.service.backend.models.mqtt.DeviceModel;
import bpr.service.backend.models.sql.CustomerEntity;

public interface ISerializer {
    String toJson(DeviceModel payload);
    String toJson(CustomerEntity payload);
    DeviceModel fromJsonToDeviceModel(String json);
    CustomerEntity fromJsonToCustomer(String json);
}
