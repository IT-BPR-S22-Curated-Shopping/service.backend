package bpr.service.backend.services.detectionAnalysisService;

import bpr.service.backend.models.dto.ProductAnalysisDto;
import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.DetectionSnapshotEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.UuidEntity;
import bpr.service.backend.persistence.repository.detectionRepository.IDetectionRepository;
import bpr.service.backend.util.exceptions.NotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DetectionAnalysisServiceProductTest {

    @Mock
    private IDetectionRepository detectionRepository;

    @InjectMocks
    private DetectionAnalysisService detectionAnalysisService;

    private final Instant instantFrom = Instant.parse("2022-05-19T08:37:14.899856700Z");
    private final Instant instantTo = Instant.parse("2022-05-20T08:37:14.899856700Z");

    private List<DetectionSnapshotEntity> repositorySnapshots;

    private final UuidEntity repositoryUUIDc1 = new UuidEntity("010D2108-0462-4F97-BAB8-000000000001");;
    private final UuidEntity repositoryUUIDc2 = new UuidEntity("010D2108-0462-4F97-BAB8-000000000002");;

    private final CustomerEntity customer1 = new CustomerEntity(List.of(repositoryUUIDc1), List.of());
    private final CustomerEntity customer2 = new CustomerEntity(List.of(repositoryUUIDc2), List.of());
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
        Long productId = 29L;
        repositoryProduct.setId(productId);
        customer1.setId(1L);
        customer2.setId(2L);

        from = instantFrom.toEpochMilli();
        to = instantTo.toEpochMilli();
    }

    private void setSnapshotsWithProducts(int amount, CustomerEntity customer, Long firstTimestamp) {
        for(int i = 0; i < amount; i++) {
            String deviceId = "bb:27:eb:02:ee:fe";
            Long locationId = 24L;
            String locationName = "Prime Beef";
            var snapshot = new DetectionSnapshotEntity(
                    firstTimestamp + (i * 1000L),
                    locationId,
                    locationName,
                    deviceId,
                    customer
            );
            snapshot.setId((long) i);
            snapshot.setProduct(repositoryProduct);
            repositorySnapshots.add(snapshot);
        }
    }

    private void setProductMock() {
        Mockito.when(detectionRepository
                .findDetectionSnapshotEntitiesByProductIdAndTimestampBetween(
                        repositoryProduct.getId(),
                        from,
                        to
                )).thenReturn(repositorySnapshots);
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

    @Test void emptySnapshotProductAnalysis() {
        // Arrange
        setProductMock();

        ProductAnalysisDto analysis = null;
        // Act
        try {
            analysis = detectionAnalysisService.productAnalysis(repositoryProduct.getId(), from, to);
        } catch (NotFoundException e) {
            assertNotNull(e);
            assertEquals(String.format("No detections for product id %s in the given timeframe.", repositoryProduct.getId()), e.getMessage());
        }
        assertNull(analysis);
    }

    @Test void productAnalysisOneCustomers() {
        // Arrange


        setSnapshotsWithProducts(6, customer1, initialTimestamp);
        setProductMock();

        // Act

        ProductAnalysisDto analysis = null;
        try {
            analysis = detectionAnalysisService.productAnalysis(repositoryProduct.getId(), from, to);
        } catch (NotFoundException e) {
            assertNull(e);
        }

        // Assert
        assertEquals(1, analysis.getTotalCustomerNo());
        assertEquals(1, analysis.getTotalNoOfVisits());
        assertEquals(getExpectedAvgMills(List.of(6)), analysis.getAvgMillisConsumed());
    }

    @Test void productAnalysisTwoCustomers() {
        // Arrange

        setSnapshotsWithProducts(6, customer1, initialTimestamp);
        setSnapshotsWithProducts(11, customer2, initialTimestamp);
        setProductMock();

        // Act
        ProductAnalysisDto analysis = null;
        try {
            analysis = detectionAnalysisService.productAnalysis(repositoryProduct.getId(), from, to);
        } catch (NotFoundException e) {
            assertNull(e);
        }
        // Assert
        assertEquals(2, analysis.getTotalCustomerNo());
        assertEquals(2, analysis.getTotalNoOfVisits());
        assertEquals(getExpectedAvgMills(List.of(6, 11)), analysis.getAvgMillisConsumed());
    }

    @Test void productAnalysisReturningCustomer() {
        // Arrange

        setSnapshotsWithProducts(6, customer1, initialTimestamp);
        setSnapshotsWithProducts(11, customer1, initialTimestamp + 10000 + 20000);
        setProductMock();

        // Act
        ProductAnalysisDto analysis = null;
        try {
            analysis = detectionAnalysisService.productAnalysis(repositoryProduct.getId(), from, to);
        } catch (NotFoundException e) {
            assertNull(e);
        }

        // Assert
        assertEquals(1, analysis.getTotalCustomerNo());
        assertEquals(2, analysis.getTotalNoOfVisits());
        assertEquals(getExpectedAvgMills(List.of(6, 11)), analysis.getAvgMillisConsumed());
    }

    @Test void productAnalysisTwoCustomersOneReturningTwice() {
        // Arrange

        setSnapshotsWithProducts(6, customer1, initialTimestamp);
        setSnapshotsWithProducts(11, customer1, initialTimestamp + 10000 + 20000);
        setSnapshotsWithProducts(11, customer2, initialTimestamp);
        setProductMock();

        // Act
        ProductAnalysisDto analysis = null;
        try {
            analysis = detectionAnalysisService.productAnalysis(repositoryProduct.getId(), from, to);
        } catch (NotFoundException e) {
            assertNull(e);
        }

        // Assert
        assertEquals(2, analysis.getTotalCustomerNo());
        assertEquals(3, analysis.getTotalNoOfVisits());
        assertEquals(getExpectedAvgMills(List.of(6, 11, 11)), analysis.getAvgMillisConsumed());
    }
}
