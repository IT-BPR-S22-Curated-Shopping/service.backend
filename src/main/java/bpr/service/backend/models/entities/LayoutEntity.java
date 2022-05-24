package bpr.service.backend.models.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "layouts")
@Data
public class LayoutEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean horizontal = true;

    // TODO: Add layouts

    public LayoutEntity() {
    }
}
