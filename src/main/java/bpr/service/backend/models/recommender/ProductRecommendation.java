package bpr.service.backend.models.recommender;

import bpr.service.backend.models.entities.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRecommendation {

    private ProductEntity product;
    private double score;
}
