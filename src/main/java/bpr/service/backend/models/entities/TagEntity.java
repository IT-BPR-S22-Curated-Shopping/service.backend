package bpr.service.backend.models.entities;


import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tags")
@Data
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String tag;

    public TagEntity() { }

    public TagEntity(String tag) {
        this.tag = tag;
    }
}
