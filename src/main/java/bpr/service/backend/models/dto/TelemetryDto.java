package bpr.service.backend.models.dto;

import lombok.Data;

@Data
public class TelemetryDto {
    private Long timestamp;
    private String deviceId;
    private String level;
    private String message;

    public TelemetryDto() {
    }

    public TelemetryDto(Long timestamp, String deviceId, String level, String message) {
        this.timestamp = timestamp;
        this.deviceId = deviceId;
        this.level = level;
        this.message = message;
    }
}
