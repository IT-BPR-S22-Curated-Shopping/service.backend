package bpr.service.backend.models.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
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
    private List<TrackerEntity> trackingDevices;

    @OneToMany
    @Column(unique=true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<PresenterEntity> presentationDevices;

    public LocationEntity() { }
}
