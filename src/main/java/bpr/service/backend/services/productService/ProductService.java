package bpr.service.backend.services.productService;

import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.persistence.repository.productRepository.IProductRepository;
import bpr.service.backend.persistence.repository.tagRepository.ITagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("ProductService")
public class ProductService implements IProductService {

    private final IProductRepository productRepository;
    private final ITagRepository tagRepository;

    public ProductService(@Autowired IProductRepository productRepository, @Autowired ITagRepository tagRepository) {
        this.productRepository = productRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public List<ProductEntity> readAll() {
        var list = new ArrayList<ProductEntity>();
        productRepository.findAll().forEach(list::add);
        return list;
    }

    @Override
    public ProductEntity readById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public ProductEntity create(ProductEntity product) {

        List<TagEntity> tags = new ArrayList<>();
        for (TagEntity tagEntity : product.getTags()) {
            TagEntity tag = tagRepository.findTagEntityByTag(tagEntity.getTag());
            if (tag == null) {
                tagEntity = tagRepository.save(tagEntity);
            } else {
                tagEntity = tag;
            }
            tags.add(tagEntity);
        }
        product.setTags(tags);


        return productRepository.save(product);
    }

    @Override
    public ProductEntity updateTags(Long id, List<String> tags) {
        ProductEntity productEntity = null;
        productEntity = productRepository.findById(id).orElse(null);
        if (productEntity != null) {
            List<TagEntity> tagList = new ArrayList<>();
            for (String tag : tags) {
                var tagEntity = tagRepository.findTagEntityByTag(tag);
                if (tagEntity == null) {
                    tagEntity = tagRepository.save(new TagEntity(tag));
                }
                tagList.add(tagEntity);
            }
            productEntity.setTags(tagList);
            productEntity = productRepository.save(productEntity);
        }

        return productEntity;
    }

}
