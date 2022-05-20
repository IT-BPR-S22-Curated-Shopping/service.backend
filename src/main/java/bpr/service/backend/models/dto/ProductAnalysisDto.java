package bpr.service.backend.models.dto;

import bpr.service.backend.models.entities.ProductEntity;
import lombok.Data;

@Data
public class ProductAnalysisDto {

    private ProductEntity product;

    private Long from;

    private Long to;

    private int totalCustomerNo;

    private int totalNoOfVisits;

    private Long avgMillisConsumed;

    public ProductAnalysisDto() {
    }

    public ProductAnalysisDto(ProductEntity product,
                              Long from,
                              Long to,
                              int totalCustomerNo,
                              int totalNoOfVisits,
                              Long avgMillisConsumed) {
        this.product = product;
        this.from = from;
        this.to = to;
        this.totalCustomerNo = totalCustomerNo;
        this.totalNoOfVisits = totalNoOfVisits;
        this.avgMillisConsumed = avgMillisConsumed;
    }
}
