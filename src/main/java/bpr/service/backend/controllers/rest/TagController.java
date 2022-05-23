package bpr.service.backend.controllers.rest;

import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.services.productService.IProductService;
import bpr.service.backend.services.tagService.ITagService;
import bpr.service.backend.util.ISerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class TagController {
    private final ITagService tagService;
    private final IProductService productService;
    private final ISerializer serializer;

    public TagController(@Autowired @Qualifier("TagService") ITagService tagService,
                         @Autowired @Qualifier("ProductService") IProductService productService,
                         @Autowired @Qualifier("JsonSerializer") ISerializer serializer) {
        this.tagService = tagService;
        this.productService = productService;
        this.serializer = serializer;
    }

//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public List<TagEntity> getAllTags() {
//        return tagService.readAll();
//    }

    @GetMapping
    public ResponseEntity<String> getAllTags() {
        return new ResponseEntity<>(serializer.toJson(tagService.readAll()), HttpStatus.OK);
    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public List<TagEntity> createTags(List<String> tags) {
//        List<TagEntity> tagEntities = new ArrayList<>();
//
//        for (String tag : tags) {
//            var tagEntity = tagService.readAll().stream().filter(x -> x.getTag().equals(tag)).findFirst().orElse(null);
//            tagEntities.add(tagEntity != null ? tagEntity : tagService.create(new TagEntity(tag)));
//        }
//
//        return tagEntities;
//    }

    @PostMapping
    public ResponseEntity<String> createTags(List<String> tags) {
        List<TagEntity> tagEntities = new ArrayList<>();

        for (String tag : tags) {
            var tagEntity = tagService.readAll().stream().filter(x -> x.getTag().equals(tag)).findFirst().orElse(null);
            tagEntities.add(tagEntity != null ? tagEntity : tagService.create(new TagEntity(tag)));
        }

        return new ResponseEntity<>(serializer.toJson(tagEntities), HttpStatus.CREATED);
    }

//    @DeleteMapping(value = "/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public void deleteTag(@PathVariable("id") Long id) {
//        var tagEntity = tagService.readById(id);
//        var productEntities = ((ProductService)productService).findAllWithTag(tagEntity);
//
//        // remove tags from products
//        for(ProductEntity productEntity : productEntities) {
//            // create new array without the tags
//            List<TagEntity> newTags = new ArrayList<>();
//            for(TagEntity tag : productEntity.getTags()) {
//                if (!tagEntity.getId().equals(tag.getId())) {
//                    newTags.add(tag);
//                }
//            }
//            productEntity.setTags(newTags);
//            productService.update(productEntity.getId(), productEntity);
//        }
//        tagService.delete(id);
//    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteTag(@PathVariable("id") Long id) {
        var tagEntity = tagService.readById(id);
        var productEntities = productService.findAllWithTag(tagEntity);

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
        return new ResponseEntity<>(String.format("Tag id %s successfully deleted", id), HttpStatus.OK);
    }

}
