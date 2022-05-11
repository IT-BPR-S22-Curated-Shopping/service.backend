package bpr.service.backend.data.dto;

import lombok.Data;

@Data
public class ConnectedDeviceDto {
    private long timestamp;
    private String companyId;
    private String deviceId;
    private String deviceType;

    public ConnectedDeviceDto() {

    }

    public ConnectedDeviceDto(long timestamp, String companyId, String deviceId, String deviceType) {
        this.timestamp = timestamp;
        this.companyId = companyId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }
}
