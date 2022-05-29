package bpr.service.backend.models.dto;

import lombok.Data;

import java.util.List;

@Data
public class LocationAnalysisDto {

    private Long from;
    private Long to;
    private Long locationId;
    private String locationName;
    private int totalCustomerNo;
    private int totalNoOfVisits;
    private Long avgMillisConsumed;
    private List<CustomerDetectionAnalysisDto> customerAnalysis;


    public LocationAnalysisDto() {
    }

    public LocationAnalysisDto(Long from,
                               Long to,
                               Long locationId,
                               String locationName,
                               int totalCustomerNo,
                               int totalNoOfVisits,
                               Long avgMillisConsumed,
                               List<CustomerDetectionAnalysisDto> customerAnalysis) {
        this.from = from;
        this.to = to;
        this.locationId = locationId;
        this.locationName = locationName;
        this.totalCustomerNo = totalCustomerNo;
        this.totalNoOfVisits = totalNoOfVisits;
        this.avgMillisConsumed = avgMillisConsumed;
        this.customerAnalysis = customerAnalysis;
    }
}
