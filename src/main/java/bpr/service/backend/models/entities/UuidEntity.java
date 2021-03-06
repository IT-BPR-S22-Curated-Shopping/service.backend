package bpr.service.backend.models.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "uuids")
@Data
public class UuidEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String uuid;

    public UuidEntity() { }

    public UuidEntity(String uuid) {
        this.uuid = uuid;
    }
}
