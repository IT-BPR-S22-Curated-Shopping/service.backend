package bpr.service.backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ISerializer {

    String toJson(Object payload) throws JsonProcessingException;
}
