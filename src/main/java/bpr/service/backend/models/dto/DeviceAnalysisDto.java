package bpr.service.backend.models.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeviceAnalysisDto {
    private Long from;
    private Long to;
    private String deviceId;
    private int totalCustomerNo;
    private int totalNoOfVisits;
    private Long avgMillisConsumed;
    private List<CustomerDetectionAnalysisDto> customerAnalysis;


    public DeviceAnalysisDto() {
    }

    public DeviceAnalysisDto(Long from,
                              Long to,
                              String deviceId,
                              int totalCustomerNo,
                              int totalNoOfVisits,
                              Long avgMillisConsumed,
                              List<CustomerDetectionAnalysisDto> customerAnalysis) {
        this.from = from;
        this.to = to;
        this.deviceId = deviceId;
        this.totalCustomerNo = totalCustomerNo;
        this.totalNoOfVisits = totalNoOfVisits;
        this.avgMillisConsumed = avgMillisConsumed;
        this.customerAnalysis = customerAnalysis;
    }
}
