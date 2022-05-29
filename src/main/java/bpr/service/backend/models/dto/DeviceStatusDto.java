package bpr.service.backend.models.dto;

import lombok.Data;

@Data
public class DeviceStatusDto {
    private Long timestamp;
    private String deviceId;
    private String state;

    public DeviceStatusDto() {
    }

    public DeviceStatusDto(Long timestamp, String deviceId, String state) {
        this.timestamp = timestamp;
        this.deviceId = deviceId;
        this.state = state;
    }
}
