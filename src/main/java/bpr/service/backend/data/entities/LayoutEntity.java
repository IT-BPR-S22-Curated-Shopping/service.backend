package bpr.service.backend.data.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "layouts")
@Data
public class LayoutEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private boolean horizontal = true;

    // TODO: Add layouts

    public LayoutEntity() {
    }
}
