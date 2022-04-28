package bpr.service.backend.util;

import bpr.service.backend.models.DeviceModel;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ISerializer {

    String toJson(DeviceModel payload) throws JsonProcessingException;

    DeviceModel fromJson(String json) throws JsonProcessingException;

}
