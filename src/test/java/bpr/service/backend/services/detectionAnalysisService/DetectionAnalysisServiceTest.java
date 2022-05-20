package bpr.service.backend.services.detectionAnalysisService;

import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.DetectionSnapshotEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.UuidEntity;
import bpr.service.backend.persistence.repository.detectionRepository.IDetectionRepository;
import bpr.service.backend.persistence.repository.productRepository.IProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DetectionAnalysisServiceTest {

    @Mock
    private IDetectionRepository detectionRepository;

    @Mock
    private IProductRepository productRepository;

    @InjectMocks
    private DetectionAnalysisService detectionAnalysisService;

    private final Instant instantFrom = Instant.parse("2022-05-19T08:37:14.899856700Z");
    private final Instant instantTo = Instant.parse("2022-05-20T08:37:14.899856700Z");

    private List<DetectionSnapshotEntity> repositorySnapshots;

    private final UuidEntity repositoryUUIDc1 = new UuidEntity("010D2108-0462-4F97-BAB8-000000000001");;
    private final UuidEntity repositoryUUIDc2 = new UuidEntity("010D2108-0462-4F97-BAB8-000000000002");;

    private CustomerEntity customer1 = new CustomerEntity(List.of(repositoryUUIDc1), List.of());
    private CustomerEntity customer2 = new CustomerEntity(List.of(repositoryUUIDc2), List.of());

    private final String deviceId = "bb:27:eb:02:ee:fe";
    private final Long productId = 29L;
    private final Long locationId = 24L;
    private final String locationName = "Prime Beef";

    private final Long initialTimestamp = 1652991171000L;


    private final ProductEntity repositoryProduct = new ProductEntity(
            "P02-3627K",
            "Wagyu"
    );

    private Long from;
    private Long to;

    @BeforeEach
    public void beforeEach() {
        repositorySnapshots = new ArrayList<>();
        repositoryProduct.setId(productId);
        customer1.setId(1L);
        customer2.setId(2L);

        from = instantFrom.toEpochMilli();
        to = instantTo.toEpochMilli();
    }

    private void setSnapShotMocks() {
        Mockito.when(
                productRepository
                        .findById(repositoryProduct.getId())).thenReturn(Optional.of(repositoryProduct));
        Mockito.when(detectionRepository
                .findDetectionSnapshotEntitiesByProductAndTimestampBetween(
                        repositoryProduct,
                        from,
                        to
                )).thenReturn(repositorySnapshots);

    }

    private void setSnapShots(int amount, CustomerEntity customer, Long firstTimestamp) {
        for(int i = 0; i < amount; i++) {
            var snapshot = new DetectionSnapshotEntity(
                    firstTimestamp + (i * 1000L),
                    locationId,
                    locationName,
                    deviceId,
                    customer
            );
            snapshot.setId((long) i);
            repositorySnapshots.add(snapshot);
        }
    }

    @Test
    public void canInstantiate() {
        assertNotNull(detectionAnalysisService);
    }

    private Long getExpectedAvgMills(List<Integer> snapshotsInSets) {
        var totalMillis = 0;
        for (var snapshot : snapshotsInSets) {
            totalMillis += (snapshot - 1) * 1000;
        }
        return (long) (totalMillis / snapshotsInSets.size());
    }

    @Test void emptySnapShotsProductAnalysisNull() {
        // Arrange
        setSnapShotMocks();

        // Act
        var analysis = detectionAnalysisService.productAnalysis(repositoryProduct.getId(), from, to);

        // Assert
        assertNull(analysis);
    }

    @Test void productAnalysisOneCustomers() {
        // Arrange

        setSnapShots(6, customer1, initialTimestamp);
        setSnapShotMocks();

        // Act
        var analysis = detectionAnalysisService.productAnalysis(repositoryProduct.getId(), from, to);

        // Assert
        assertNotNull(analysis);
        assertEquals(1, analysis.getTotalCustomerNo());
        assertEquals(1, analysis.getTotalNoOfVisits());
        assertEquals(getExpectedAvgMills(List.of(6)), analysis.getAvgMillisConsumed());
    }


    @Test void productAnalysisTwoCustomers() {
        // Arrange

        setSnapShots(6, customer1, initialTimestamp);
        setSnapShots(11, customer2, initialTimestamp);
        setSnapShotMocks();

        // Act
        var analysis = detectionAnalysisService.productAnalysis(repositoryProduct.getId(), from, to);

        // Assert
        assertNotNull(analysis);
        assertEquals(2, analysis.getTotalCustomerNo());
        assertEquals(2, analysis.getTotalNoOfVisits());
        assertEquals(getExpectedAvgMills(List.of(6, 11)), analysis.getAvgMillisConsumed());
    }

    @Test void productAnalysisReturningCustomer() {
        // Arrange

        setSnapShots(6, customer1, initialTimestamp);
        setSnapShots(11, customer1, initialTimestamp + 10000 + 20000);
        setSnapShotMocks();

        // Act
        var analysis = detectionAnalysisService.productAnalysis(repositoryProduct.getId(), from, to);

        // Assert
        assertNotNull(analysis);
        assertEquals(1, analysis.getTotalCustomerNo());
        assertEquals(2, analysis.getTotalNoOfVisits());
        assertEquals(getExpectedAvgMills(List.of(6, 11)), analysis.getAvgMillisConsumed());
    }

    @Test void productAnalysisTwoCustomersOneReturningTwice() {
        // Arrange

        setSnapShots(6, customer1, initialTimestamp);
        setSnapShots(11, customer1, initialTimestamp + 10000 + 20000);
        setSnapShots(11, customer2, initialTimestamp);
        setSnapShotMocks();

        // Act
        var analysis = detectionAnalysisService.productAnalysis(repositoryProduct.getId(), from, to);

        // Assert
        assertNotNull(analysis);
        assertEquals(2, analysis.getTotalCustomerNo());
        assertEquals(3, analysis.getTotalNoOfVisits());
        assertEquals(getExpectedAvgMills(List.of(6, 11, 11)), analysis.getAvgMillisConsumed());
    }


}
