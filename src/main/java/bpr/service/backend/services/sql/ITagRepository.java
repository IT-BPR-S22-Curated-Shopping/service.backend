package bpr.service.backend.services.sql;

import bpr.service.backend.models.sql.TagEntity;

import java.util.Optional;

public interface ITagRepository {
    TagEntity addTag(TagEntity tag);

    Iterable<TagEntity> findAllTags();

    TagEntity findByTag(String tag);

    Optional<TagEntity> findTagById(Long id);
}
