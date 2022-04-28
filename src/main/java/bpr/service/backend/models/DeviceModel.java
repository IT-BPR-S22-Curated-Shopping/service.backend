package bpr.service.backend.models;

import lombok.Getter;

import java.io.Serializable;
import java.util.Date;


public class DeviceModel implements Serializable {

    @Getter
    private String uuid;
    @Getter
    private String deviceId;
    @Getter
    private Date time;

    public DeviceModel() {
    }

    public DeviceModel(String uuid, String deviceId, Date time) {
        this.uuid = uuid;
        this.deviceId = deviceId;
        this.time = time;
    }

    @Override
    public String toString() {
        return "DeviceModel{" +
                "uuid='" + uuid + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", time=" + time +
                '}';
    }
}
