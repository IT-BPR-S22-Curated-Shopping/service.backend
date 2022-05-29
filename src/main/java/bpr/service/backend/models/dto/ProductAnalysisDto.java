package bpr.service.backend.models.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductAnalysisDto {

    private Long from;
    private Long to;
    private Long productId;
    private String productNo;
    private String productName;
    private int totalCustomerNo;
    private int totalNoOfVisits;
    private Long avgMillisConsumed;
    private List<CustomerDetectionAnalysisDto> customerAnalysis;


    public ProductAnalysisDto() {
    }

    public ProductAnalysisDto(Long from,
                              Long to,
                              Long productId,
                              String productNo,
                              String productName,
                              int totalCustomerNo,
                              int totalNoOfVisits,
                              Long avgMillisConsumed,
                              List<CustomerDetectionAnalysisDto> customerAnalysis) {
        this.from = from;
        this.to = to;
        this.productId = productId;
        this.productNo = productNo;
        this.productName = productName;
        this.totalCustomerNo = totalCustomerNo;
        this.totalNoOfVisits = totalNoOfVisits;
        this.avgMillisConsumed = avgMillisConsumed;
        this.customerAnalysis = customerAnalysis;
    }
}
