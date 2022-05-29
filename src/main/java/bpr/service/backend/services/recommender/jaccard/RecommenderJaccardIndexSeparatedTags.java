//package bpr.service.backend.services.recommender.jaccard;
//
//import bpr.service.backend.managers.events.Event;
//import bpr.service.backend.managers.events.IEventManager;
//import bpr.service.backend.models.dto.RecommendationDto;
//import bpr.service.backend.models.entities.*;
//import bpr.service.backend.models.recommender.ProductRecommendation;
//import bpr.service.backend.services.data.ICRUDService;
//import bpr.service.backend.services.recommender.IRecommender;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//
//import java.beans.PropertyChangeEvent;
//import java.util.*;
//
//public class RecommenderJaccardIndexSeparatedTags implements IRecommender {
//
//    private final IEventManager eventManager;
//    private final ICRUDService<ProductEntity> productService;
//    private final List<ProductEntity> products;
//
//
//    public RecommenderJaccardIndexSeparatedTags(IEventManager eventManager, ICRUDService<ProductEntity> productService, List<ProductEntity> products) {
//        this.eventManager = eventManager;
//        this.productService = productService;
//        this.products = products;
//    }
//
//    public RecommenderJaccardIndexSeparatedTags(@Autowired IEventManager eventManager,
//                                                @Autowired @Qualifier("ProductService") ICRUDService<ProductEntity> productService) {
//        this.eventManager = eventManager;
//        this.productService = productService;
//        products = productService.readAll();
////        eventManager.addListener(Event.CUSTOMER_LOCATED, this::customerLocated);
//    }
//
//    private void customerLocated(PropertyChangeEvent propertyChangeEvent) {
//        DetectionSnapshotEntity dse = (DetectionSnapshotEntity) propertyChangeEvent.getNewValue();
//        var recommendedScores = recommend(dse.getCustomer(), dse.getProduct());
//        var dto = new RecommendationDto(dse.getCustomer(), dse.getProduct(), dse.getIdDeviceId(), recommendedScores);
//        eventManager.invoke(Event.NEW_RECOMMENDATION, dto);
//    }
//
//    @Override
//    public List<ProductRecommendation> recommend(CustomerEntity customer, ProductEntity product) {
//
//        List<ProductRecommendation> customerProductRecommendations = new ArrayList<>();
//        for (ProductEntity p : products) {
//            var similarity = JaccardSimilarityIndex.findJaccardSimilarityIndexDivideByTwo(customer.getTags(), p.getTags());
//            customerProductRecommendations.add(new ProductRecommendation(p, similarity));
//        }
//
//        List<ProductRecommendation> productProductRecommendations = new ArrayList<>();
//        for (ProductEntity p : products) {
//            var similarity = JaccardSimilarityIndex.findJaccardSimilarityIndexDivideByTwo(product.getTags(), p.getTags());
//            productProductRecommendations.add(new ProductRecommendation(p, similarity));
//        }
//
//        Map<String, ProductRecommendation> recommendationMap = new HashMap<>();
//        addToMap(customerProductRecommendations, recommendationMap);
//        addToMap(productProductRecommendations, recommendationMap);
//
//
//        // add map to a list
//        List<ProductRecommendation> recommendationArray = new ArrayList<>(recommendationMap.values());
//        recommendationArray.sort(Comparator.comparingDouble(ProductRecommendation::getScore).reversed());
//
//        // remove self product from list
//        recommendationArray.removeIf(x->x.getProduct().equals(product));
//
//        return recommendationArray;
//    }
//
//    @Override
//    public List<ProductRecommendation> buildRecommendations(CustomerEntity customer) {
//        return null;
//    }
//
//    private void addToMap(List<ProductRecommendation> recommendationList, Map<String, ProductRecommendation> recommendationMap) {
//        for (ProductRecommendation j : recommendationList) {
//            String productId = j.getProduct().getName();
//            if (recommendationMap.containsKey(productId)) {
//                j.setScore(j.getScore() + recommendationMap.get(productId).getScore());
//            }
//            recommendationMap.put(productId, j);
//        }
//    }
//}
