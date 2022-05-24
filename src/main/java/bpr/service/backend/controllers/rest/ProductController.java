package bpr.service.backend.controllers.rest;

import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.services.tagService.ITagService;
import bpr.service.backend.services.productService.IProductService;
import bpr.service.backend.util.ISerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final IProductService productService;
    private final ITagService tagService;

    private final ISerializer serializer;

    public ProductController(@Autowired @Qualifier("ProductService") IProductService productService,
                             @Autowired @Qualifier("TagService") ITagService tagService,
                             @Autowired @Qualifier("JsonSerializer") ISerializer serializer) {
        this.productService = productService;
        this.tagService = tagService;
        this.serializer = serializer;
    }

//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public List<ObjectNode> getAllProducts() {
//        var products = productService.readAll();
//
//        ObjectMapper mapper = new ObjectMapper();
//        List<ObjectNode> nodes = new ArrayList<>();
//
//        for (ProductEntity entity : products) {
//            ObjectNode node = mapper.createObjectNode();
//            node.put("id", entity.getId());
//            node.put("productNo", entity.getNumber());
//            node.put("name", entity.getName());
//            nodes.add(node);
//        }
//
//        return nodes;
//    }

    @GetMapping
    public ResponseEntity<String> getAllProducts() {
        var products = productService.readAll();

        ObjectMapper mapper = new ObjectMapper();
        List<ObjectNode> nodes = new ArrayList<>();

        for (ProductEntity entity : products) {
            ObjectNode node = mapper.createObjectNode();
            node.put("id", entity.getId());
            node.put("number", entity.getNumber());
            node.put("name", entity.getName());
            nodes.add(node);
        }

        return new ResponseEntity<>(serializer.toJson(nodes), HttpStatus.OK);
    }
//    @GetMapping(value = "/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public ProductEntity getProductById(@PathVariable("id") Long id) {
//        return productService.readById(id);
//    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<String> getProductById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(serializer.toJson(productService.readById(id)), HttpStatus.OK);
    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public ProductEntity createProduct(@RequestBody ProductEntity product) {
//        List<TagEntity> tags = new ArrayList<>();
//        for (TagEntity tagEntity : product.getTags()) {
//            TagEntity tag = ((TagService) tagService).findByTag(tagEntity.getTag());
//            if (tag == null) {
//                tagEntity = tagService.create(tagEntity);
//            } else {
//                tagEntity = tag;
//            }
//            tags.add(tagEntity);
//        }
//        product.setTags(tags);
//        return productService.create(product);
//    }

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody ProductEntity product) {
        List<TagEntity> tags = new ArrayList<>();
        for (TagEntity tagEntity : product.getTags()) {
            TagEntity tag = tagService.findByTag(tagEntity.getTag());
            if (tag == null) {
                tagEntity = tagService.create(tagEntity);
            } else {
                tagEntity = tag;
            }
            tags.add(tagEntity);
        }
        product.setTags(tags);
        return new ResponseEntity<>(serializer.toJson(productService.create(product)), HttpStatus.CREATED);
    }

//    @PutMapping(value = "/{id}")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public ProductEntity updateTags(@PathVariable("id") Long id, @RequestBody List<String> tags) {
//        var entity = productService.readById(id);
//
//        if (entity != null) {
//            List<TagEntity> tagList = new ArrayList<>();
//            for (String tag : tags) {
//                var tagEntity = ((TagService) tagService).findByTag(tag);
//                if (tagEntity == null) {
//                    tagEntity = tagService.create(new TagEntity(tag));
//                }
//                tagList.add(tagEntity);
//            }
//            entity.setTags(tagList);
//        }
//        if (entity != null)
//            return productService.update(entity.getId(), entity);
//        return null;
//    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<String> updateTags(@PathVariable("id") Long id, @RequestBody List<String> tags) {
        var entity = productService.readById(id);

        if (entity != null) {
            List<TagEntity> tagList = new ArrayList<>();
            for (String tag : tags) {
                var tagEntity = tagService.findByTag(tag);
                if (tagEntity == null) {
                    tagEntity = tagService.create(new TagEntity(tag));
                }
                tagList.add(tagEntity);
            }
            entity.setTags(tagList);
        }
        if (entity != null)
            return new ResponseEntity<>(serializer.toJson(productService.update(entity.getId(), entity)), HttpStatus.ACCEPTED);
        return null;
    }
}
