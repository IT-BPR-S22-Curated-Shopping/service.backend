package bpr.service.backend.persistence.repository.tagRepository;

import bpr.service.backend.persistence.repository.entities.TagEntity;
import org.springframework.data.repository.Repository;

public interface ITagRepository extends Repository<TagEntity, Long> {

    TagEntity findByTag(String name);

}
