package bpr.service.backend.controllers.rest;

import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.services.data.ICRUDService;
import bpr.service.backend.services.data.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class TagController {
    private final ICRUDService<TagEntity> tagService;
    private final ICRUDService<ProductEntity> productService;

    public TagController(@Autowired @Qualifier("TagService") ICRUDService<TagEntity> tagService,
                         @Autowired @Qualifier("ProductService") ICRUDService<ProductEntity> productService) {
        this.tagService = tagService;
        this.productService = productService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TagEntity> getAllTags() {
        return tagService.readAll();
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<TagEntity> createTags(List<String> tags) {
        List<TagEntity> tagEntities = new ArrayList<>();

        for (String tag : tags) {
            var tagEntity = tagService.readAll().stream().filter(x -> x.getTag().equals(tag)).findFirst().orElse(null);
            tagEntities.add(tagEntity != null ? tagEntity : tagService.create(new TagEntity(tag)));
        }

        return tagEntities;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTag(@PathVariable("id") Long id) {
        var tagEntity = tagService.readById(id);
        var productEntities = ((ProductService)productService).findAllWithTag(tagEntity);

        // remove tags from products
        for(ProductEntity productEntity : productEntities) {
            // create new array without the tags
            List<TagEntity> newTags = new ArrayList<>();
            for(TagEntity tag : productEntity.getTags()) {
                if (!tagEntity.getId().equals(tag.getId())) {
                    newTags.add(tag);
                }
            }
            productEntity.setTags(newTags);
            productService.update(productEntity.getId(), productEntity);
        }
        tagService.delete(id);
    }

}
