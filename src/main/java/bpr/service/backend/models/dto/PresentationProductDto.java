package bpr.service.backend.models.dto;

import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.recommender.ProductRecommendation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PresentationProductDto {
    private String sessionId;
    private Long locationId;
    private ProductEntity currentProduct;
    private List<ProductRecommendation> recommendations;
}
