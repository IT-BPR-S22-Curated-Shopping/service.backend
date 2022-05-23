package bpr.service.backend.models.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class CustomerDetectionAnalysisDto {
    private Long customerId;
    private Long customerAvgMillis;
    private List<VisitDetectionDto> visits;

    public CustomerDetectionAnalysisDto(Long customerId, List<Long> customerTimestamps) {
        this.customerId = customerId;
        visits = new ArrayList<>();
        seperateTimestampsIntoVisits(customerTimestamps);
        setCustomerAvg();
    }

    private void seperateTimestampsIntoVisits(List<Long> timestamps) {
        int maxSecondsDifference = 10000; // 10 Seconds;
        var detectionSequence = new ArrayList<Long>();
        for (var timestamp : timestamps) {
            if (detectionSequence.size() == 0) {
                detectionSequence.add(timestamp);
            }
            else {
                if (timestamp < (detectionSequence.get(detectionSequence.size() - 1) + maxSecondsDifference)) {
                    detectionSequence.add(timestamp);
                }
                else {
                    visits.add(new VisitDetectionDto(detectionSequence));
                    detectionSequence.clear();
                    detectionSequence.add(timestamp);
                }
            }
        }
        visits.add(new VisitDetectionDto(detectionSequence));
    }

    private void setCustomerAvg() {
        var total = 0L;
        for (var visit : visits) {
            total += visit.getDurationMillis();
        }
        customerAvgMillis = total / visits.size();
    }

}
