package bpr.service.backend.models.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "id_devices")
@Data
public class IdentificationDeviceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Date created;

    private String companyId;

    @Column(unique=true)
    private String deviceId;

    private String deviceType;

    private Long timestampOffline;
    private Long timestampOnline;
    private Long timestampReady;
    private Long timestampActive;

    public IdentificationDeviceEntity() {
    }

    public IdentificationDeviceEntity(String companyId, String deviceId, String deviceType, Long timestampOffline) {
        this.companyId = companyId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.timestampOffline = timestampOffline;
        this.timestampOnline = 0L;
        this.timestampActive = 0L;
        this.timestampReady = 0L;
    }
}
