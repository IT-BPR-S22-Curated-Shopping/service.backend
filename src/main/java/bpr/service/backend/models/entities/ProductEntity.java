package bpr.service.backend.models.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String number;

    private String name;

    private String image;

    private String caption;

    private String description;

    private double price;

    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<ProductEntity> relatedProducts;

    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.MERGE)
    private List<TagEntity> tags;

    public ProductEntity() {
        relatedProducts = new ArrayList<>();
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
