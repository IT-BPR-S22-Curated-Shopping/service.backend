package bpr.service.backend.models.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;

@Entity
@Table(name = "trackers")
@Data
public class TrackerEntity {
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

    public TrackerEntity() { }

    public TrackerEntity(String companyId, String deviceId, String deviceType) {
        this.companyId = companyId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }
}
