package bpr.service.backend.services.productService;

import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;

import java.util.List;

public interface IProductService {
    ProductEntity readById(Long id);
    List<ProductEntity> readAll();
    ProductEntity create(ProductEntity entity);
    ProductEntity update(Long id, ProductEntity entity);
    List<ProductEntity> findAllWithTag(TagEntity tagEntity);
}
