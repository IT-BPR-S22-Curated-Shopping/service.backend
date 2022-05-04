package bpr.service.backend.persistence.repository.tagRepository;

import bpr.service.backend.models.sql.TagEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

public interface ITagRepository extends Repository<TagEntity, Long> {

    TagEntity findByTag(String name);

}
