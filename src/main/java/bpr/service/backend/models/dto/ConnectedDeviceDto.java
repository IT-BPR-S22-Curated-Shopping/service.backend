package bpr.service.backend.models.dto;

import lombok.Data;

@Data
public class ConnectedDeviceDto {
    private Long timestamp;
    private String companyId;
    private String deviceId;
    private String deviceType;

    public ConnectedDeviceDto() {

    }

    public ConnectedDeviceDto(Long timestamp, String companyId, String deviceId, String deviceType) {
        this.timestamp = timestamp;
        this.companyId = companyId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }
}
