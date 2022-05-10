package bpr.service.backend.util;

import bpr.service.backend.models.DeviceModel;
import bpr.service.backend.persistence.repository.entities.CustomerEntity;

public interface ISerializer {
    String toJson(DeviceModel payload);
    String toJson(CustomerEntity payload);
    DeviceModel fromJsonToDeviceModel(String json);
    CustomerEntity fromJsonToCustomer(String json);
}
