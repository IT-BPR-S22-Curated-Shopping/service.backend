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
        ProductEntity productEntity = productService.readById(id);
        return productEntity;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductEntity createProduct(@RequestBody ProductEntity product) {

        var storedTags = tagService.readAll();
        for (TagEntity entity :product.getTags()) {
            for (TagEntity storedTag : storedTags) {
                if (!entity.getTag().equals(storedTag.getTag())) {
                    entity = tagService.create(entity);
                } else {
                    entity = storedTag;
                }
            }
        }
        ProductEntity productEntity = productService.create(product);
        return productEntity;

    }
}
