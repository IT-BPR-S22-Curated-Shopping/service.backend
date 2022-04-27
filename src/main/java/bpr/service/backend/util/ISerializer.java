package bpr.service.backend.util;

import bpr.service.backend.MqttMessage;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ISerializer {

    String toJson(String payload) throws JsonProcessingException;
    String toJson(MqttMessage payload) throws JsonProcessingException;
}
