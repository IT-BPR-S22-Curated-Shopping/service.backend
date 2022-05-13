package bpr.service.backend.models.dto;

import lombok.Data;

@Data
public class DeviceStatusDto {
    private long timestamp;
    private String deviceId;
    private boolean online;

    public DeviceStatusDto() {
    }

    public DeviceStatusDto(long timestamp, String deviceId, boolean online) {
        this.timestamp = timestamp;
        this.deviceId = deviceId;
        this.online = online;
    }
}
