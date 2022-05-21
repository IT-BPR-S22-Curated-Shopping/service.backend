package bpr.service.backend.services.recommender;

import bpr.service.backend.models.recommender.ProductRecommendation;
import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.ProductEntity;

import java.util.List;

public interface IRecommender {

    List<ProductRecommendation> recommend(CustomerEntity customer, ProductEntity product);
}
