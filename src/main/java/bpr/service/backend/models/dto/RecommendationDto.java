package bpr.service.backend.models.dto;


import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.recommender.ProductRecommendation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {

    private CustomerEntity customer;
    private ProductEntity product;
    private String deviceId;

    private List<ProductRecommendation> recommendations;
}



