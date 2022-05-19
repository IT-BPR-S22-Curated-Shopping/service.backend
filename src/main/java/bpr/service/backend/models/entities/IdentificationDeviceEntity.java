package bpr.service.backend.models.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "id_devices")
@Data
public class IdentificationDeviceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CreationTimestamp
    private Instant created;

    private String companyId;

    @Column(unique=true)
    private String deviceId;

    private String deviceType;

    private long timestampOffline;
    private long timeStampOnline;
    private long timestampReady;
    private long timestampActive;

    public IdentificationDeviceEntity() { }

    public IdentificationDeviceEntity(String companyId, String deviceId, String deviceType, long timestampOffline) {
        this.companyId = companyId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.timestampOffline = timestampOffline;
    }
}
