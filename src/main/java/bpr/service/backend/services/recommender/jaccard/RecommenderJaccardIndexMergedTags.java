package bpr.service.backend.services.recommender.jaccard;


import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.PresentationProductDto;
import bpr.service.backend.models.dto.RecommendationDto;
import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.DetectionSnapshotEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.models.recommender.ProductRecommendation;
import bpr.service.backend.services.locationService.ILocationService;
import bpr.service.backend.services.productService.IProductService;
import bpr.service.backend.services.recommender.IRecommender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.beans.PropertyChangeEvent;
import java.util.*;

@Service("RecommenderJaccardIndexMergedTags")
public class RecommenderJaccardIndexMergedTags implements IRecommender {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IEventManager eventManager;
    private final IProductService productService;
    private final ILocationService locationService;
    private List<ProductEntity> products;

    public RecommenderJaccardIndexMergedTags(@Autowired IEventManager eventManager,
                                             @Autowired @Qualifier("ProductService") IProductService productService,
                                             @Autowired @Qualifier("LocationService") ILocationService locationService) {
        this.eventManager = eventManager;
        this.productService = productService;
        this.locationService = locationService;
        products = productService.readAll();

        eventManager.addListener(Event.CUSTOMER_LOCATED, this::customerLocated);
        eventManager.addListener(Event.INIT_RECOMMENDATION, this::getRecommendationBasedOnProduct);
    }

    private void getRecommendationBasedOnProduct(PropertyChangeEvent propertyChangeEvent) {
        var dto = (PresentationProductDto) propertyChangeEvent.getNewValue();
        if (dto != null) {
            CustomerEntity blankCustomer = new CustomerEntity();
            blankCustomer.setTags(new ArrayList<>());
            var locationEntity = locationService.readById(dto.getLocationId());
            if (locationEntity != null) {
                var recommendations = recommend(blankCustomer, locationEntity.getProduct(), 10);
                if (recommendations != null) {
                    dto.setRecommendations(recommendations);
                    dto.setCurrentProduct(locationEntity.getProduct());
                    eventManager.invoke(Event.CURRENT_PRODUCT_RECOMMENDATION, dto);
                } else {
                    logger.error("Could not find any recommendations to initially show presentation device.");
                }
            } else {
                logger.error("No location found for initial product recommendation.");
            }
        } else {
            logger.error("No dto found for initial product recommendation.");
        }
    }

    public void setProducts(List<ProductEntity> products) {
        this.products = products;
    }

    private void customerLocated(PropertyChangeEvent propertyChangeEvent) {
        DetectionSnapshotEntity dse = (DetectionSnapshotEntity) propertyChangeEvent.getNewValue();
        var recommendedScores = recommend(dse.getCustomer(), dse.getProduct(), 10);
        var dto = new RecommendationDto(dse.getCustomer(), dse.getProduct(), dse.getLocationId(), recommendedScores);
        eventManager.invoke(Event.NEW_RECOMMENDATION, dto);
    }

    @Override
    public List<ProductRecommendation> recommend(CustomerEntity customer, ProductEntity product, int size) {
        if (customer != null && customer.getTags() != null && product != null && product.getTags() != null) {
            List<ProductRecommendation> recommendations = new ArrayList<>();
            Set<TagEntity> set = new LinkedHashSet<>(customer.getTags());
            set.addAll(product.getTags());
            customer.setTags(new ArrayList<>(set));

            for (ProductEntity p : products) {
                var similarity = JaccardSimilarityIndex.findJaccardSimilarityIndex(customer.getTags(), p.getTags());
                recommendations.add(new ProductRecommendation(p, similarity));
            }
            // sort
            recommendations.sort(Comparator.comparingDouble(ProductRecommendation::getScore).reversed());

            // resize array
            List<ProductRecommendation> result = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                result.add(recommendations.get(i));
            }

            // remove input product from list
            result.removeIf(x -> x.getProduct().getName().equals(product.getName()));

            return result;
        }
        return null;
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
