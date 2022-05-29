package bpr.service.backend.services.tagService;

import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.persistence.repository.productRepository.IProductRepository;
import bpr.service.backend.persistence.repository.tagRepository.ITagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("TagService")
public class TagService implements ITagService {

    private final ITagRepository tagRepository;
    private final IProductRepository productRepository;

    public TagService(@Autowired ITagRepository tagRepository, @Autowired IProductRepository productRepository) {
        this.tagRepository = tagRepository;
        this.productRepository = productRepository;
    }


    @Override
    public TagEntity findByTag(String tag) {
        return tagRepository.findTopByTagEquals(tag);
    }

    @Override
    public List<TagEntity> readAll() {
        var tags = new ArrayList<TagEntity>();
        tagRepository.findAll().forEach(tags::add);
        return tags;
    }

    @Override
    public TagEntity readById(Long id) {
        return tagRepository.findById(id).orElse(null);
    }

    @Override
    public TagEntity create(TagEntity entity) {
        return tagRepository.save(entity);
    }

    public List<TagEntity> createTags(List<String> tags) {
        List<TagEntity> tagEntities = new ArrayList<>();
        for (String tag : tags) {
            var tagEntity = tagRepository.findTagEntityByTag(tag);
            if (tagEntity == null) {
                tagEntity = tagRepository.save(new TagEntity(tag));
            }
            tagEntities.add(tagEntity);
        }
        return tagEntities;
    }

    @Override
    public void delete(Long id) {
        var tagEntity = tagRepository.findById(id).orElse(null);
        if (tagEntity != null) {
            var productEntities = productRepository.findByTags(tagEntity);

            // remove tags from products
            for (ProductEntity productEntity : productEntities) {
                // create new array without the tags
                List<TagEntity> newTags = new ArrayList<>();
                for (TagEntity tag : productEntity.getTags()) {
                    if (!tagEntity.getId().equals(tag.getId())) {
                        newTags.add(tag);
                    }
                }
                productEntity.setTags(newTags);
                productRepository.save(productEntity);
            }
            tagRepository.delete(tagEntity);
            tagRepository.findById(id).ifPresent(tagRepository::delete);
        }
    }
}
