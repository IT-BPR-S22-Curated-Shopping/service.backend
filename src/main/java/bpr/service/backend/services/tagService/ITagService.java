package bpr.service.backend.services.tagService;

import bpr.service.backend.models.entities.TagEntity;

import java.util.List;

public interface ITagService {
    TagEntity create(TagEntity entity);
    List<TagEntity> createTags(List<String> tags);
    List<TagEntity> readAll();
    TagEntity readById(Long id);
    void delete(Long id);
}
