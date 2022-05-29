package bpr.service.backend.controllers.rest;

import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.services.productService.IProductService;
import bpr.service.backend.services.tagService.ITagService;
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

    @GetMapping
    public ResponseEntity<String> getAllProducts() {
        ResponseEntity<String> response;
        var products = productService.readAll();

        if (products != null && products.size() > 0) {
//            ObjectMapper mapper = new ObjectMapper();
//            List<ObjectNode> nodes = new ArrayList<>();
//
//            for (ProductEntity entity : products) {
//                ObjectNode node = mapper.createObjectNode();
//                node.put("id", entity.getId());
//                node.put("number", entity.getNumber());
//                node.put("name", entity.getName());
//                nodes.add(node);
//            }
            response = new ResponseEntity<>(serializer.toJson(products), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("No products found.", HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<String> getProductById(@PathVariable("id") Long id) {
        ResponseEntity<String> response;
        if (id != 0) {
            ProductEntity product = productService.readById(id);
            if (product != null) {
                response = new ResponseEntity<>(serializer.toJson(product), HttpStatus.OK);
            } else {
                response = new ResponseEntity<>("Could not find a product with matching id", HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>("Invalid id. ", HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody ProductEntity product) {
        ResponseEntity<String> response;
        if (product != null) {
            if (product.getName().isEmpty() || product.getNumber().isEmpty()) {
                response = new ResponseEntity<>("Cannot create a product without name or number", HttpStatus.BAD_REQUEST);
            } else {
                var productEntity = productService.create(product);
                if (productEntity != null) {
                    response = new ResponseEntity<>(serializer.toJson(productEntity), HttpStatus.CREATED);
                } else{
                    response = new ResponseEntity<>("Could not create product.", HttpStatus.NO_CONTENT);
                }
            }
        } else {
            response = new ResponseEntity<>("No product provided", HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<String> updateTags(@PathVariable("id") Long id, @RequestBody List<String> tags) {
        ResponseEntity<String> response;
        if (id != 0) {
            if (tags != null && tags.size() > 0) {
                var product = productService.updateTags(id, tags);
                if (product != null) {
                    response = new ResponseEntity<>(serializer.toJson(product), HttpStatus.ACCEPTED);
                } else {
                    response = new ResponseEntity<>("Cannot update tag on the product.", HttpStatus.BAD_GATEWAY);
                }
            } else {
                response = new ResponseEntity<>("Tags must be included to be able to update product with tags.", HttpStatus.BAD_REQUEST);
            }
        } else {
            response = new ResponseEntity<>("Invalid ID", HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @PutMapping()
    public ResponseEntity<String> updateProduct(@RequestBody ProductEntity product) {
        if (product == null) {
            return new ResponseEntity<>("Body cannot be empty", HttpStatus.BAD_REQUEST);
        }
        else {
            var updated = productService.update(product);
            if (updated == null) {
                return new ResponseEntity<>("Unable to update the product", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(serializer.toJson(updated), HttpStatus.OK);
        }

    }
}
