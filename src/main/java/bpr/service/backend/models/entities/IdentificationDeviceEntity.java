package bpr.service.backend.models.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;

@Entity
@Table(name = "trackers")
@Data
public class IdentificationDeviceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date created;

    private String companyId;

    @Column(unique=true)
    private String deviceId;

    private String deviceType;

    public IdentificationDeviceEntity() { }

    public IdentificationDeviceEntity(String companyId, String deviceId, String deviceType) {
        this.companyId = companyId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }
}
