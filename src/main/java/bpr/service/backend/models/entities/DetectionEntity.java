package bpr.service.backend.models.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@Table(name = "detections")
@Data
public class DetectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private long timestamp;

    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private ProductEntity product;

    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private CustomerEntity customer;

    public DetectionEntity() {
    }
}
