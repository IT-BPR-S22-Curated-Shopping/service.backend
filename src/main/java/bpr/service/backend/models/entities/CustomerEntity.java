package bpr.service.backend.models.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
public class CustomerEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<UuidEntity> uuids;

    @ManyToMany
    @Column(unique=true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TagEntity> tags;

    public CustomerEntity() {
    }

    public CustomerEntity(List<UuidEntity> uuids, List<TagEntity> tags) {
        this.uuids = uuids;
        this.tags = tags;
    }
}
