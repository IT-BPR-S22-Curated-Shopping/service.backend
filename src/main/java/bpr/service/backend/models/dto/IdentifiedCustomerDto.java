package bpr.service.backend.models.dto;

import bpr.service.backend.models.entities.CustomerEntity;
import lombok.Data;

@Data
public class IdentifiedCustomerDto {

    private long timestamp;
    private CustomerEntity customer;
    private String identificationDeviceId;

    public IdentifiedCustomerDto() {
    }

    public IdentifiedCustomerDto(long timestamp, CustomerEntity customer, String identificationDeviceId) {
        this.timestamp = timestamp;
        this.customer = customer;
        this.identificationDeviceId = identificationDeviceId;
    }
}
