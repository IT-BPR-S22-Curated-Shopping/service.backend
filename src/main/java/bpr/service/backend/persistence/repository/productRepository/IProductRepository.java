package bpr.service.backend.persistence.repository.productRepository;

import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends CrudRepository<ProductEntity, Long> {
    List<ProductEntity> findByTags(TagEntity tagEntity);
}
