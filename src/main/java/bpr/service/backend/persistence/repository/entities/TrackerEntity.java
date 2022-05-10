package bpr.service.backend.persistence.repository.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "trackers")
@Data
public class TrackerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String deviceId;

    public TrackerEntity() { }
}
