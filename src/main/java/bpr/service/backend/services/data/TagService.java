package bpr.service.backend.services.data;

import bpr.service.backend.models.entities.LocationEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.persistence.repository.tagRepository.ITagRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("TagService")
public class TagService implements ICRUDService<TagEntity>{


    private final ITagRepository tagRepository;

    public TagService(ITagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<TagEntity> readAll() {
        var tags = new ArrayList<TagEntity>();
        tagRepository.findAll().forEach(tags::add);
        return tags;
    }

    @Override
    public TagEntity readById(Long id) {
        return null;
    }

    @Override
    public TagEntity create(TagEntity entity) {
        return tagRepository.save(entity);
    }

    @Override
    public TagEntity update(Long id, TagEntity entity) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
