package bpr.service.backend.models.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
public class CustomerEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<UuidEntity> uuids;

    @ManyToMany
    @Column(unique=true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<TagEntity> tags;

    public CustomerEntity() {
    }

    public CustomerEntity(List<UuidEntity> uuid, List<TagEntity> tags) {
        this.uuids = uuid;
        this.tags = tags;
    }
}
