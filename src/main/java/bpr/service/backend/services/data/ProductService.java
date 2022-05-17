package bpr.service.backend.services.data;

import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.persistence.repository.productRepository.IProductRepository;
import bpr.service.backend.persistence.repository.tagRepository.ITagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("ProductService")
public class ProductService implements ICRUDService<ProductEntity> {


    private final IProductRepository productRepository;

    public ProductService(@Autowired IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductEntity> findAllWithTag(TagEntity tagEntity) {
        return productRepository.findByTags(tagEntity);
    }

    @Override
    public List<ProductEntity> readAll() {
        var list = new ArrayList<ProductEntity>();
        productRepository.findAll().forEach(list::add);
        return list;
    }

    @Override
    public ProductEntity readById(Long id) {
        Optional<ProductEntity> entity = productRepository.findById(id);
        return entity.orElse(null);
    }

    @Override
    public ProductEntity create(ProductEntity entity) {
        return productRepository.save(entity);
    }

    @Override
    public ProductEntity update(Long id, ProductEntity entity) {
        return productRepository.save(entity);
    }

    @Override
    public void delete(Long id) {

    }


}
