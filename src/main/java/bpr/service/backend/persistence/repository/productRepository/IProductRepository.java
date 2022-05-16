package bpr.service.backend.persistence.repository.productRepository;

import bpr.service.backend.models.entities.ProductEntity;
import org.springframework.data.repository.CrudRepository;

public interface IProductRepository extends CrudRepository<ProductEntity, Long> {

}
