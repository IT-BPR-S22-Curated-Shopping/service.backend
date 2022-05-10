package bpr.service.backend.data.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class DetectionModel implements Serializable {

    private long timestamp;
    private String uuid;
    // TODO add device

    public DetectionModel() {
    }
}
