package bpr.service.backend.models.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String number;

    private String name;

    private String name;

    private String image;

    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<ProductEntity> relatedProducts;

    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<TagEntity> tags;

    public ProductEntity() {
        relatedProducts = new ArrayList<>();
        tags = new ArrayList<>();
    }
}
