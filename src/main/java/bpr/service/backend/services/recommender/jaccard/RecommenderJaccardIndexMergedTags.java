package bpr.service.backend.services.recommender.jaccard;


import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.RecommendationDto;
import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.DetectionSnapshotEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.models.recommender.ProductRecommendation;
import bpr.service.backend.services.data.ICRUDService;
import bpr.service.backend.services.recommender.IRecommender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.beans.PropertyChangeEvent;
import java.util.*;

@Service("RecommenderJaccardIndexMergedTags")
public class RecommenderJaccardIndexMergedTags implements IRecommender {

    private final IEventManager eventManager;
    private final ICRUDService<ProductEntity> productService;
    private List<ProductEntity> products;


    public RecommenderJaccardIndexMergedTags(@Autowired IEventManager eventManager,
                                             @Autowired @Qualifier("ProductService") ICRUDService<ProductEntity> productService) {
        this.eventManager = eventManager;
        this.productService = productService;
        products = productService.readAll();

//        eventManager.addListener(Event.CUSTOMER_LOCATED, this::customerLocated);
    }

    public void setProducts(List<ProductEntity> products) {
        this.products = products;
    }

    private void customerLocated(PropertyChangeEvent propertyChangeEvent) {
        DetectionSnapshotEntity dse = (DetectionSnapshotEntity) propertyChangeEvent.getNewValue();
        var recommendedScores = recommend(dse.getCustomer(), dse.getProduct());
        var dto = new RecommendationDto(dse.getCustomer(), dse.getProduct(), dse.getIdDeviceId(), recommendedScores);
        eventManager.invoke(Event.NEW_RECOMMENDATION, dto);
    }

    @Override
    public List<ProductRecommendation> recommend(CustomerEntity customer, ProductEntity product) {

        List<ProductRecommendation> recommendations = new ArrayList<>();
        Set<TagEntity> set = new LinkedHashSet<>(customer.getTags());
        set.addAll(product.getTags());
        customer.setTags(new ArrayList<>(set));

        for (ProductEntity p : products) {
            var similarity = JaccardSimilarityIndex.findJaccardSimilarityIndex(customer.getTags(), p.getTags());
            recommendations.add(new ProductRecommendation(p, similarity));
        }

        recommendations.sort(Comparator.comparingDouble(ProductRecommendation::getScore).reversed());

        // remove input product from list
        recommendations.removeIf(x -> x.getProduct().getName().equals(product.getName()));
        return recommendations;
    }

    @Override
    public List<ProductEntity> getProfileProducts(CustomerEntity customer, int size) {
        products = productService.readAll();


        List<ProductRecommendation> recommendations = new ArrayList<>();

        for (ProductEntity p : products) {
            var similarity = JaccardSimilarityIndex.findJaccardSimilarityIndex(customer.getTags(), p.getTags());
            recommendations.add(new ProductRecommendation(p, 1 - similarity));
        }

        recommendations.sort(Comparator.comparingDouble(ProductRecommendation::getScore).reversed());

        List<ProductEntity> profileProducts = new ArrayList<>();

        for (int i = 0; i < Math.min(products.size(), size); i++) {
            profileProducts.add(recommendations.get(i).getProduct());
        }


        return profileProducts;
    }


}
