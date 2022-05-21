package bpr.service.backend.models.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "detection_snapshots")
@Data
public class DetectionSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long timestamp;
    private Long locationId;
    private String locationName;

    @OneToOne
    private ProductEntity product;

    @OneToOne
    private CustomerEntity customer;

    private String idDeviceId;


    public DetectionSnapshotEntity() {
    }

    public DetectionSnapshotEntity(Long timestamp,
                                   Long locationId,
                                   String locationName,
                                   String idDeviceId,
                                   CustomerEntity customer) {
        this.timestamp = timestamp;
        this.locationId = locationId;
        this.locationName = locationName;
        this.idDeviceId = idDeviceId;
        this.customer = customer;
    }



}
