package bpr.service.backend.models.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "locations")
@Data
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne
    private ProductEntity product;

    @OneToMany
    private List<IdentificationDeviceEntity> identificationDevices;

    public LocationEntity() {
        identificationDevices = new ArrayList<>();
    }

    public LocationEntity(String name, ProductEntity product, List<IdentificationDeviceEntity> identificationDevices) {
        this.name = name;
        this.product = product;
        this.identificationDevices = identificationDevices;
    }
}
