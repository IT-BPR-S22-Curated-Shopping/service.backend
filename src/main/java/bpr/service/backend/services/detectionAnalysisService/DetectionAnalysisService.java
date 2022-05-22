package bpr.service.backend.services.detectionAnalysisService;

import bpr.service.backend.models.dto.CustomerDetectionAnalysisDto;
import bpr.service.backend.models.dto.LocationAnalysisDto;
import bpr.service.backend.models.dto.ProductAnalysisDto;
import bpr.service.backend.models.entities.DetectionSnapshotEntity;
import bpr.service.backend.persistence.repository.detectionRepository.IDetectionRepository;
import bpr.service.backend.util.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("DetectionAnalysisService")
public class DetectionAnalysisService implements IDetectionAnalysisService {

    private final IDetectionRepository detectionRepository;

    public DetectionAnalysisService(@Autowired IDetectionRepository detectionRepository) {
        this.detectionRepository = detectionRepository;
    }

    private List<Long> getCustomersIdsIn(List<DetectionSnapshotEntity> snapshots) {
        var customers = new ArrayList<Long>();
        for (var snapshot : snapshots) {
            if (!customers.contains(snapshot.getCustomer().getId())) {
                customers.add(snapshot.getCustomer().getId());
            }
        }
        return customers;
    }

    private List<Long> getTimestampsForCustomerIn(List<DetectionSnapshotEntity> snapshots, Long customerId) {
        var timestamps = new ArrayList<Long>();
        for (var snapshot : snapshots) {
            if (Objects.equals(snapshot.getCustomer().getId(), customerId)) {
                timestamps.add(snapshot.getTimestamp());
            }
        }
        return timestamps;
    }

    private Map<String, Long> calculateAverageVisitIn(List<CustomerDetectionAnalysisDto> customerAnalysis) {
        var totalMillis = 0L;
        var visitCount = 0L;
        for (var analysis : customerAnalysis) {
            for (var visit : analysis.getVisits()) {
                visitCount ++;
                totalMillis += visit.getDurationMillis();
            }
        }
        var averageVisit = new HashMap<String, Long>();
        averageVisit.put("visitCount", visitCount);
        averageVisit.put("avgMillis", totalMillis / visitCount);
        return averageVisit;
    }

    private List<CustomerDetectionAnalysisDto> createAnalysisForAllCustomersIn(List<DetectionSnapshotEntity> snapshots) {
        var customerIds = getCustomersIdsIn(snapshots);
        var customerAnalysis = new ArrayList<CustomerDetectionAnalysisDto>();
        for (var id : customerIds) {
            customerAnalysis.add(new CustomerDetectionAnalysisDto(
                    id,
                    getTimestampsForCustomerIn(snapshots, id)
            ));
        }
        return customerAnalysis;
    }

    private ProductAnalysisDto preformProductAnalysis(Long from, Long to, List<DetectionSnapshotEntity> snapshots) {
        var analysis = createAnalysisForAllCustomersIn(snapshots);
        var avgVisit = calculateAverageVisitIn(analysis);

        return new ProductAnalysisDto(
                from,
                to,
                snapshots.get(0).getProduct().getId(),
                snapshots.get(0).getProduct().getNumber(),
                snapshots.get(0).getProduct().getName(),
                analysis.size(),
                avgVisit.get("visitCount").intValue(),
                avgVisit.get("avgMillis"),
                analysis
        );
    }

    private LocationAnalysisDto preformLocationAnalysis(Long from, Long to, List<DetectionSnapshotEntity> snapshots) {
        var analysis = createAnalysisForAllCustomersIn(snapshots);
        var avgVisit = calculateAverageVisitIn(analysis);

        return new LocationAnalysisDto(
                from,
                to,
                snapshots.get(0).getLocationId(),
                snapshots.get(0).getLocationName(),
                analysis.size(),
                avgVisit.get("visitCount").intValue(),
                avgVisit.get("avgMillis"),
                analysis
        );
    }

    @Override
    public ProductAnalysisDto productAnalysis(Long productId, Long from, Long to) throws NotFoundException {
        var snapshots = detectionRepository.findDetectionSnapshotEntitiesByProductIdAndTimestampBetween(productId, from, to);
        if (snapshots == null || snapshots.isEmpty()) {
            throw new NotFoundException(String.format("No detections for product id %s in the given timeframe.", productId));
        }
        return preformProductAnalysis(from, to, snapshots);
    }

    @Override
    public LocationAnalysisDto locationAnalysis(Long locationId, Long from, Long to) throws NotFoundException {
        var snapshots = detectionRepository.findDetectionSnapshotEntitiesByLocationIdAndTimestampBetween(locationId, from, to);

        if (snapshots == null || snapshots.isEmpty()) {
            throw new NotFoundException(String.format("No detections for location id %s in the given timeframe.", locationId));
        }

        return preformLocationAnalysis(from, to, snapshots);
    }

    @Override
    public LocationAnalysisDto locationAnalysis(String locationName, Long from, Long to) throws NotFoundException {
        var snapshots = detectionRepository.findDetectionSnapshotEntitiesByLocationNameAndTimestampBetween(locationName, from, to);

        if (snapshots == null || snapshots.isEmpty()) {
            throw new NotFoundException(String.format("No detections for location with name %s in the given timeframe.", locationName));
        }

        return preformLocationAnalysis(from, to, snapshots);
    }
}