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

    private Long timestampOffline;
    private Long timeStampOnline;
    private Long timestampReady;
    private Long timestampActive;

    public IdentificationDeviceEntity() {
    }

    public IdentificationDeviceEntity(String companyId, String deviceId, String deviceType, Long timestampOffline) {
        this.companyId = companyId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.timestampOffline = timestampOffline;
        this.timeStampOnline = 0L;
        this.timestampActive = 0L;
        this.timestampReady = 0L;
    }
}
