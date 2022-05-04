package bpr.service.backend.persistence.sql;

import bpr.service.backend.models.sql.TagEntity;
import org.springframework.data.repository.CrudRepository;

public interface TagRepository extends CrudRepository<TagEntity, Long> {

    TagEntity findByTag(String name);
}
