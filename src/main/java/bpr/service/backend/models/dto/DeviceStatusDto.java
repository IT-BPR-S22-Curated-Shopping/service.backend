package bpr.service.backend.models.dto;

import lombok.Data;

@Data
public class DeviceStatusDto {
    private long timestamp;
    private String deviceId;
    private String status;

    public DeviceStatusDto() {
    }

    public DeviceStatusDto(long timestamp, String deviceId, String status) {
        this.timestamp = timestamp;
        this.deviceId = deviceId;
        this.status = status;
    }
}
