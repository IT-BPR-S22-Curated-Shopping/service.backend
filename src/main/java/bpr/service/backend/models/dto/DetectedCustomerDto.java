package bpr.service.backend.models.dto;

import lombok.Data;

@Data
public class DetectedCustomerDto {
    private Long timestamp;
    private String deviceId;
    private String uuid;

    public DetectedCustomerDto() {
    }

    public DetectedCustomerDto(Long timestamp, String deviceId, String uuid) {
        this.timestamp = timestamp;
        this.deviceId = deviceId;
        this.uuid = uuid;
    }
}
