package bpr.service.backend.services.detectionAnalysisService;

import bpr.service.backend.models.dto.ProductAnalysisDto;
import bpr.service.backend.models.entities.DetectionSnapshotEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.persistence.repository.detectionRepository.IDetectionRepository;
import bpr.service.backend.persistence.repository.productRepository.IProductRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("DetectionAnalysisService")
public class DetectionAnalysisService implements IDetectionAnalysisService {

    private final IDetectionRepository detectionRepository;
    private final IProductRepository productRepository;

    public DetectionAnalysisService(@Autowired IDetectionRepository detectionRepository,
                                    @Autowired IProductRepository productRepository) {
        this.detectionRepository = detectionRepository;
        this.productRepository = productRepository;
    }

    private Optional<ProductEntity> findProduct(Long productId) {
        return productRepository.findById(productId);
    }

    private List<Long> getCustomersIds(List<DetectionSnapshotEntity> snapshots) {
        var customers = new ArrayList<Long>();
        for (var snapshot : snapshots) {
            if (!customers.contains(snapshot.getCustomer().getId())) {
                customers.add(snapshot.getCustomer().getId());
            }
        }
        return customers;
    }

    private List<Long> getTimestampsForCustomer(List<DetectionSnapshotEntity> snapshots, Long customerId) {
        var timestamps = new ArrayList<Long>();
        for (var snapshot : snapshots) {
            if (Objects.equals(snapshot.getCustomer().getId(), customerId)) {
                timestamps.add(snapshot.getTimestamp());
            }
        }
        return timestamps;
    }

    private Map<String, Long> calculateAverageVisit(List<CustomerAnalysis> customerAnalysis) {
        var totalMillis = 0L;
        var visitCount = 0L;
        for (var analysis : customerAnalysis) {
            for (var visit : analysis.visits) {
                visitCount ++;
                totalMillis += visit.duration;
            }
        }
        var averageVisit = new HashMap<String, Long>();
        averageVisit.put("visitCount", visitCount);
        averageVisit.put("avgMillis", totalMillis / visitCount);
        return averageVisit;
    }

    private List<CustomerAnalysis> createAnalysisForAllCustomers(List<DetectionSnapshotEntity> snapshots) {
        var customerIds = getCustomersIds(snapshots);
        var customerAnalysis = new ArrayList<CustomerAnalysis>();
        for (var id : customerIds) {
            customerAnalysis.add(new CustomerAnalysis(
                    id,
                    getTimestampsForCustomer(snapshots, id)
            ));
        }
        return customerAnalysis;
    }

    @Override
    public ProductAnalysisDto productAnalysis(Long productId, Long from, Long to) {
        var product = findProduct(productId);
        if (product.isEmpty()) {
            return null;
        }
        var snapshots = detectionRepository.findDetectionSnapshotEntitiesByProductAndTimestampBetween(product.get(), from, to);

        if (snapshots == null || snapshots.isEmpty()) {
            return null;
        }

        var analysis = createAnalysisForAllCustomers(snapshots);
        var avgVisit = calculateAverageVisit(analysis);

        return new ProductAnalysisDto(
                product.get(),
                from,
                to,
                analysis.size(),
                avgVisit.get("visitCount").intValue(),
                avgVisit.get("avgMillis")
        );
    }

    @Data
    private static class CustomerAnalysis {
        private Long customerId;
        private List<Long> customerSnapshots;
        private List<Visit> visits;

        public CustomerAnalysis(Long customerId, List<Long> customerSnapshots) {
            this.customerId = customerId;
            this.customerSnapshots = customerSnapshots;
            visits = new ArrayList<>();
            seperateTimestampsIntoVisits(customerSnapshots);
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
                        visits.add(new Visit(detectionSequence));
                        detectionSequence.clear();
                        detectionSequence.add(timestamp);
                    }
                }
            }
            visits.add(new Visit(detectionSequence));
        }
    }

    @Data
    private static class Visit {
        private List<Long> timestamps;
        private Long firstTimestamp;
        private Long lastTimestamp;
        private Long duration;

        public Visit() {
        }

        public Visit(List<Long> timestamps) {
            this.timestamps = new ArrayList<>(timestamps);
            firstTimestamp = timestamps.get(0);
            lastTimestamp = timestamps.get(timestamps.size() - 1);
            duration = lastTimestamp - firstTimestamp;
        }
    }
}