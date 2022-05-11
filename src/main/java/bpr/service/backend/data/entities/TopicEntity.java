package bpr.service.backend.data.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "topics")
@Data
public class TopicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String topic;

    public TopicEntity() { }
}
