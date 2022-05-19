package bpr.service.backend.models.dto;

import bpr.service.backend.models.entities.CustomerEntity;
import lombok.Data;

@Data
public class IdentifiedCustomerDto {

    private Long timestamp;
    private CustomerEntity customer;
    private String identificationDeviceId;

    public IdentifiedCustomerDto() {
    }

    public IdentifiedCustomerDto(Long timestamp, CustomerEntity customer, String identificationDeviceId) {
        this.timestamp = timestamp;
        this.customer = customer;
        this.identificationDeviceId = identificationDeviceId;
    }
}
