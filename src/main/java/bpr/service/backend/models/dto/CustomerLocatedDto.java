package bpr.service.backend.models.dto;

import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.LocationEntity;
import lombok.Data;

@Data
public class CustomerLocatedDto {

    private long timestamp;
    private CustomerEntity customer;
    private LocationEntity location;

    public CustomerLocatedDto() {
    }

    public CustomerLocatedDto(long timestamp, CustomerEntity customer, LocationEntity location) {
        this.timestamp = timestamp;
        this.customer = customer;
        this.location = location;
    }
}
