package bpr.service.backend.models.dto;

import lombok.Data;

@Data
public class ConnectedDeviceErrorDto {

    private Long timestamp;
    private ConnectedDeviceDto device;
    private String message;

    public ConnectedDeviceErrorDto() {
    }

    public ConnectedDeviceErrorDto(Long timestamp, ConnectedDeviceDto device, String message) {
        this.timestamp = timestamp;
        this.device = device;
        this.message = message;
    }
}
