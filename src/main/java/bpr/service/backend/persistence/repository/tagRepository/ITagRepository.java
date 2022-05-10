package bpr.service.backend.persistence.repository.tagRepository;

import bpr.service.backend.data.entities.TagEntity;
import org.springframework.data.repository.CrudRepository;

public interface ITagRepository extends CrudRepository<TagEntity, Long> {

    TagEntity findByTag(String name);
}
