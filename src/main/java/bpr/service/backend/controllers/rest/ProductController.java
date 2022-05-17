package bpr.service.backend.controllers.rest;

import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.services.data.ICRUDService;
import bpr.service.backend.services.data.TagService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ICRUDService<ProductEntity> productService;
    private final ICRUDService<TagEntity> tagService;


    public ProductController(@Autowired @Qualifier("ProductService") ICRUDService<ProductEntity> productService,
                             @Autowired @Qualifier("TagService") ICRUDService<TagEntity> tagService) {
        this.productService = productService;
        this.tagService = tagService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ObjectNode> getAllProducts() {
        var products = productService.readAll();

        ObjectMapper mapper = new ObjectMapper();
        List<ObjectNode> nodes = new ArrayList<>();

        for (ProductEntity entity : products) {
            ObjectNode node = mapper.createObjectNode();
            node.put("id", entity.getId());
            node.put("productNo", entity.getProductNo());
            node.put("name", entity.getName());
            nodes.add(node);
        }

        return nodes;
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductEntity getProductById(@PathVariable("id") Long id) {
        return productService.readById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductEntity createProduct(@RequestBody ProductEntity product) {
        List<TagEntity> tags = new ArrayList<>();
        for (TagEntity tagEntity : product.getTags()) {
            TagEntity tag = ((TagService) tagService).findByTag(tagEntity.getTag());
            if (tag == null) {
                tagEntity = tagService.create(tagEntity);
            } else {
                tagEntity = tag;
            }
            tags.add(tagEntity);
        }
        product.setTags(tags);
        return productService.create(product);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ProductEntity updateTags(@PathVariable("id") Long id, @RequestBody List<String> tags) {
        var entity = productService.readById(id);

        if (entity != null) {
            List<TagEntity> tagList = new ArrayList<>();
            for (String tag : tags) {
                var tagEntity = ((TagService) tagService).findByTag(tag);
                if (tagEntity == null) {
                    tagEntity = tagService.create(new TagEntity(tag));
                }
                tagList.add(tagEntity);
            }
            entity.setTags(tagList);
        }
        if (entity != null)
            return productService.update(entity.getId(), entity);
        return null;
    }
}
