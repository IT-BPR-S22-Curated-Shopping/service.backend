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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private ProductEntity product;

    @OneToMany
    @Column(unique=true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<IdentificationDeviceEntity> identificationDevices;

    @OneToMany
    @Column(unique=true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<PresenterEntity> presentationDevices;

    public LocationEntity() {
        identificationDevices = new ArrayList<>();
        presentationDevices = new ArrayList<>();
    }

    public LocationEntity(String name, ProductEntity product, List<IdentificationDeviceEntity> identificationDevices, List<PresenterEntity> presentationDevices) {
        this.name = name;
        this.product = product;
        this.identificationDevices = identificationDevices;
        this.presentationDevices = presentationDevices;
    }
}
