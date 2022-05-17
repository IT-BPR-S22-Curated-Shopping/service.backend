package bpr.service.backend.persistence.repository.productRepository;

import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IProductRepository extends CrudRepository<ProductEntity, Long> {
    List<ProductEntity> findByTags(TagEntity tagEntity);
}
