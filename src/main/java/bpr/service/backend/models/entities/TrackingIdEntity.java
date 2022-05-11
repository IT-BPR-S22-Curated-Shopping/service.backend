package bpr.service.backend.models.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tracking_ids")
@Data
public class TrackingIdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique=true)
    private String uuid;

    public TrackingIdEntity() { }
}
