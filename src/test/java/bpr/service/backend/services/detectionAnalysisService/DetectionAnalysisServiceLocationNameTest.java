package bpr.service.backend.services.detectionAnalysisService;

import bpr.service.backend.models.dto.LocationAnalysisDto;
import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.DetectionSnapshotEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.UuidEntity;
import bpr.service.backend.persistence.repository.detectionRepository.IDetectionRepository;
import bpr.service.backend.util.detectionAnalysisService.DetectionAnalysisService;
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
class DetectionAnalysisServiceLocationNameTest {

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

    private final Long locationId = 24L;
    private final String locationName = "Prime Beef";

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

    private void setSnapshotsWithoutProducts(int amount, CustomerEntity customer, Long firstTimestamp) {
        for(int i = 0; i < amount; i++) {
            String deviceId = "bb:27:eb:02:ee:fe";
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

    private void setLocationIdMock() {
        Mockito.when(detectionRepository
                .findDetectionSnapshotEntitiesByLocationNameAndTimestampBetween(
                        locationName,
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

    @Test void emptySnapshotLocationAnalysis() {
        // Arrange
        setLocationIdMock();

        LocationAnalysisDto analysis = null;
        // Act
        try {
            analysis = detectionAnalysisService.locationAnalysis(locationName, from, to);
        } catch (NotFoundException e) {
            assertNotNull(e);
            assertEquals(String.format("No detections for location with name %s in the given timeframe.", locationName), e.getMessage());
        }
        assertNull(analysis);
    }

    @Test void locationAnalysisOneCustomers(){
        // Arrange
        setSnapshotsWithProducts(6, customer1, initialTimestamp);
        setLocationIdMock();

        // Act

        LocationAnalysisDto analysis = null;
        // Act
        try {
            analysis = detectionAnalysisService.locationAnalysis(locationName, from, to);
        } catch (NotFoundException e) {
            assertNull(e);
        }

        // Assert
        assertEquals(1, analysis.getTotalCustomerNo());
        assertEquals(1, analysis.getTotalNoOfVisits());
        assertEquals(getExpectedAvgMills(List.of(6)), analysis.getAvgMillisConsumed());
    }

    @Test void locationAnalysisTwoCustomers() {
        // Arrange

        setSnapshotsWithProducts(6, customer1, initialTimestamp);
        setSnapshotsWithProducts(11, customer2, initialTimestamp);
        setLocationIdMock();

        LocationAnalysisDto analysis = null;
        // Act
        try {
            analysis = detectionAnalysisService.locationAnalysis(locationName, from, to);
        } catch (NotFoundException e) {
            assertNull(e);
        }

        // Assert
        assertEquals(2, analysis.getTotalCustomerNo());
        assertEquals(2, analysis.getTotalNoOfVisits());
        assertEquals(getExpectedAvgMills(List.of(6, 11)), analysis.getAvgMillisConsumed());
    }

    @Test void locationAnalysisReturningCustomer(){
        // Arrange

        setSnapshotsWithProducts(6, customer1, initialTimestamp);
        setSnapshotsWithProducts(11, customer1, initialTimestamp + 10000 + 20000);
        setLocationIdMock();

        LocationAnalysisDto analysis = null;
        // Act
        try {
            analysis = detectionAnalysisService.locationAnalysis(locationName, from, to);
        } catch (NotFoundException e) {
            assertNull(e);
        }

        // Assert
        assertEquals(1, analysis.getTotalCustomerNo());
        assertEquals(2, analysis.getTotalNoOfVisits());
        assertEquals(getExpectedAvgMills(List.of(6, 11)), analysis.getAvgMillisConsumed());
    }

    @Test void locationAnalysisTwoCustomersOneReturningTwice() {
        // Arrange

        setSnapshotsWithProducts(6, customer1, initialTimestamp);
        setSnapshotsWithProducts(11, customer1, initialTimestamp + 10000 + 20000);
        setSnapshotsWithProducts(11, customer2, initialTimestamp);
        setLocationIdMock();

        LocationAnalysisDto analysis = null;
        // Act
        try {
            analysis = detectionAnalysisService.locationAnalysis(locationName, from, to);
        } catch (NotFoundException e) {
            assertNull(e);
        }

        // Assert
        assertEquals(2, analysis.getTotalCustomerNo());
        assertEquals(3, analysis.getTotalNoOfVisits());
        assertEquals(getExpectedAvgMills(List.of(6, 11, 11)), analysis.getAvgMillisConsumed());
    }

    @Test void locationAnalysisTwoCustomersOneReturningTwiceNoProduct() {
        // Arrange

        setSnapshotsWithoutProducts(6, customer1, initialTimestamp);
        setSnapshotsWithoutProducts(11, customer1, initialTimestamp + 10000 + 20000);
        setSnapshotsWithoutProducts(11, customer2, initialTimestamp);
        setLocationIdMock();

        LocationAnalysisDto analysis = null;
        // Act
        try {
            analysis = detectionAnalysisService.locationAnalysis(locationName, from, to);
        } catch (NotFoundException e) {
            assertNull(e);
        }

        // Assert
        assertEquals(2, analysis.getTotalCustomerNo());
        assertEquals(3, analysis.getTotalNoOfVisits());
        assertEquals(getExpectedAvgMills(List.of(6, 11, 11)), analysis.getAvgMillisConsumed());
    }
}
