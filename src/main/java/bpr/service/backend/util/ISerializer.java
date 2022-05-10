package bpr.service.backend.util;

import bpr.service.backend.data.models.DeviceModel;
import bpr.service.backend.data.entities.CustomerEntity;

public interface ISerializer {
    String toJson(DeviceModel payload);
    String toJson(CustomerEntity payload);
    DeviceModel fromJsonToDeviceModel(String json);
    CustomerEntity fromJsonToCustomer(String json);
}
