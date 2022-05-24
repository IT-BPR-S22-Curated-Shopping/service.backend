package bpr.service.backend.models.entities;

import lombok.Data;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@Table(name = "presenters")
@Data
public class PresenterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private LayoutEntity layout;

    public PresenterEntity() {
    }
}
