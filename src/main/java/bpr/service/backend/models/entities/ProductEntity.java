package bpr.service.backend.models.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@Transactional
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String number;

    private String name;

    @Column(columnDefinition="TEXT")
    private String image;

    private String caption;

    private String description;

    private double price;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TagEntity> tags;

    public ProductEntity() {
        tags = new ArrayList<>();
    }

    public ProductEntity(String productNo, String name) {
        this.number = productNo;
        this.name = name;
    }

    public ProductEntity(String productNo, String name, String image, List<TagEntity> tags) {
        this.number = productNo;
        this.name = name;
        this.image = image;
        this.tags = tags;
    }

}
