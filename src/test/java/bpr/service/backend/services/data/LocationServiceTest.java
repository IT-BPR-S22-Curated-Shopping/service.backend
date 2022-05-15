package bpr.service.backend.services.data;

import bpr.service.backend.models.entities.LocationEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TrackerEntity;
import bpr.service.backend.persistence.repository.locationRepository.ILocationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private ILocationRepository locationRepository;

    @InjectMocks
    LocationService locationService;

    private LocationEntity locationEntity;

    @BeforeEach
    public void beforeEach() {
        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setProductNo("testProductNo");

        TrackerEntity device = new TrackerEntity("010d2108","ff:27:eb:02:ee:ff","BLE");
        List<TrackerEntity> deviceList = new ArrayList<>() {{add(device);}};

        locationEntity = new LocationEntity();
        locationEntity.setId(3L);
        locationEntity.setName("testLocation");
        locationEntity.setProduct(product);
        locationEntity.setTrackingDevices(deviceList);
    }

    @Test
    public void ReadAll_AddedLocation_ExpectListWithLocation() {
        // Arrange
        List<LocationEntity> locationList = new ArrayList<>();
        locationList.add(locationEntity);
        Mockito.when(locationRepository.findAll()).thenReturn(locationList);

        // Act
        List<LocationEntity> resultList = locationService.ReadAll();

        // Assert
        Mockito.verify(locationRepository, Mockito.times(1)).findAll();
        Assertions.assertEquals(locationList, resultList);
    }

    @Test
    public void ReadAll_NoLocations_ExpectEmptyList() {
        // Arrange
        List<LocationEntity> locationList = new ArrayList<>();
        Mockito.when(locationRepository.findAll()).thenReturn(locationList);

        // Act
        List<LocationEntity> resultList = locationService.ReadAll();

        // Assert
        Mockito.verify(locationRepository, Mockito.times(1)).findAll();
        Assertions.assertEquals(locationList, resultList);
    }

    @Test
    public void ReadById_AddedLocation_ExpectLocationReturned() {
        // Arrange
        Mockito.when(locationRepository.findById(3L)).thenReturn(Optional.of(locationEntity));

        // Act
        LocationEntity result = locationService.ReadById(3L);

        // Assert
        Mockito.verify(locationRepository, Mockito.times(2)).findById(3L);
        Assertions.assertEquals(locationEntity, result);
    }

    @Test
    public void ReadById_NoLocations_ExpectNullValue() {
        // Arrange
        // Act
        LocationEntity result = locationService.ReadById(3L);

        // Assert
        Mockito.verify(locationRepository, Mockito.times(1)).findById(3L);
        Assertions.assertNull(result);
    }

    @Test
    public void Create_ValidLocation_ExpectLocationAdded() {
        // Arrange
        Mockito.when(locationRepository.save(any())).thenReturn(locationEntity);

        // Act
        LocationEntity result = locationService.Create(locationEntity);

        // Assert
        Mockito.verify(locationRepository, Mockito.times(1)).save(locationEntity);
        Assertions.assertEquals(locationEntity, result);
    }

    @Test
    public void Update_ValidLocation_ExpectLocationUpdated() {
        // Arrange
        var updatedLocation = locationEntity;
        updatedLocation.setName("updatedTestLocation");
        Mockito.when(locationRepository.findById(3L)).thenReturn(Optional.of(locationEntity));
        Mockito.when(locationRepository.save(any())).thenReturn(updatedLocation);

        // Act
        LocationEntity result = locationService.Update(3L, updatedLocation);

        // Assert
        Mockito.verify(locationRepository, Mockito.times(1)).save(updatedLocation);
        Assertions.assertEquals(updatedLocation, result);
    }

    @Test
    public void Delete_ValidLocationId_ExpectLocationDeleted() {
        // Arrange
        // Act
        locationService.Delete(3L);

        // Assert
        Mockito.verify(locationRepository, Mockito.times(1)).deleteById(3L);
    }

}