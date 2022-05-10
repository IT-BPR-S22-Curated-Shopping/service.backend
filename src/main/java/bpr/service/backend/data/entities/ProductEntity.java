package bpr.service.backend.data.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "products")
@Data
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String productNo;

    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<ProductEntity> relatedProducts;

    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<TagEntity> tags;

    //TODO: Add images and information.

    public ProductEntity() { }
}
