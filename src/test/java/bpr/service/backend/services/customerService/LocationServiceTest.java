package bpr.service.backend.services.customerService;

import bpr.service.backend.managers.events.EventManager;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.models.entities.LocationEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.persistence.repository.locationRepository.ILocationRepository;
import bpr.service.backend.persistence.repository.productRepository.IProductRepository;
import bpr.service.backend.services.locationService.LocationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Spy
    private IEventManager eventManager = new EventManager();

    @Mock
    private ILocationRepository locationRepository;

    @Mock
    private IProductRepository productRepository;

    @InjectMocks
    LocationService locationService;

    private LocationEntity locationEntity;

    private final long timestamp = 1652463743476L;

    @BeforeEach
    public void beforeEach() {
        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setNumber("testProductNo");

        IdentificationDeviceEntity device = new IdentificationDeviceEntity("010d2108","ff:27:eb:02:ee:ff","BLE", timestamp);
        List<IdentificationDeviceEntity> deviceList = new ArrayList<>() {{add(device);}};

        locationEntity = new LocationEntity();
        locationEntity.setId(3L);
        locationEntity.setName("testLocation");
        locationEntity.setProduct(product);
        locationEntity.setIdentificationDevices(deviceList);
    }

    @Test
    public void ReadAll_AddedLocation_ExpectListWithLocation() {
        // Arrange
        List<LocationEntity> locationList = new ArrayList<>();
        locationList.add(locationEntity);
        Mockito.when(locationRepository.findAll()).thenReturn(locationList);

        // Act
        List<LocationEntity> resultList = locationService.readAll();

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
        List<LocationEntity> resultList = locationService.readAll();

        // Assert
        Mockito.verify(locationRepository, Mockito.times(1)).findAll();
        Assertions.assertEquals(locationList, resultList);
    }

    @Test
    public void ReadById_AddedLocation_ExpectLocationReturned() {
        // Arrange
        Mockito.when(locationRepository.findById(3L)).thenReturn(Optional.of(locationEntity));

        // Act
        LocationEntity result = locationService.readById(3L);

        // Assert
        Mockito.verify(locationRepository, Mockito.times(2)).findById(3L);
        Assertions.assertEquals(locationEntity, result);
    }

    @Test
    public void ReadById_NoLocations_ExpectNullValue() {
        // Arrange
        // Act
        LocationEntity result = locationService.readById(3L);

        // Assert
        Mockito.verify(locationRepository, Mockito.times(1)).findById(3L);
        Assertions.assertNull(result);
    }

    @Test
    public void Create_ValidLocation_ExpectLocationAdded() {
        // Arrange
        Mockito.when(locationRepository.save(any())).thenReturn(locationEntity);

        // Act
        LocationEntity result = locationService.create(locationEntity);

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
        LocationEntity result = locationService.update(3L, updatedLocation);

        // Assert
        Mockito.verify(locationRepository, Mockito.times(1)).save(updatedLocation);
        Assertions.assertEquals(updatedLocation, result);
    }

    @Test
    public void UpdateWithDeviceList_ValidLocationId_NonEmptyList_ExpectDeviceListAdded() {
        // Arrange
        IdentificationDeviceEntity device = new IdentificationDeviceEntity("010d2108","ff:00:ab:03:ef:ff","BLE", timestamp);

        List<IdentificationDeviceEntity> deviceList = new ArrayList<>() {{add(device);}};

        var expectedLocation = locationEntity;
        expectedLocation.setIdentificationDevices(deviceList);

        Mockito.when(locationRepository.findById(5L)).thenReturn(Optional.of(locationEntity));
        Mockito.when(locationRepository.save(any())).thenReturn(expectedLocation);

        // Act
        LocationEntity result = locationService.updateWithDeviceList(5L, deviceList);

        // Assert
        Mockito.verify(locationRepository, Mockito.times(1)).save(expectedLocation);
        Assertions.assertEquals(expectedLocation, result);
    }

    @Test
    public void UpdateWithDeviceList_ValidLocationId_EmptyList_ExpectEmptyDeviceList() {
        // Arrange
        List<IdentificationDeviceEntity> deviceList = new ArrayList<>();

        var expectedLocation = locationEntity;
        expectedLocation.setIdentificationDevices(deviceList);

        Mockito.when(locationRepository.findById(5L)).thenReturn(Optional.of(locationEntity));
        Mockito.when(locationRepository.save(any())).thenReturn(expectedLocation);

        // Act
        LocationEntity result = locationService.updateWithDeviceList(5L, deviceList);

        // Assert
        Mockito.verify(locationRepository, Mockito.times(1)).save(expectedLocation);
        Assertions.assertEquals(expectedLocation, result);

    }

    @Test
    public void UpdateWithDeviceList_InvalidLocationId_NonEmptyList_ExpectException() {
        // Arrange
        IdentificationDeviceEntity device = new IdentificationDeviceEntity("010d2108","ff:00:ab:03:ef:ff","BLE", timestamp);
        List<IdentificationDeviceEntity> deviceList = new ArrayList<>() {{add(device);}};

        var expectedLocation = locationEntity;
        expectedLocation.setIdentificationDevices(deviceList);

        Mockito.when(locationRepository.findById(5L)).thenThrow(new MockitoException("Location not found"));

        // Act
        // Assert
        Assertions.assertThrows(MockitoException.class, () -> {
            locationService.updateWithDeviceList(5L, deviceList);
        });
    }

    @Test
    public void UpdateWithDeviceList_InvalidLocationId_EmptyList_ExpectException() {
        // Arrange
        List<IdentificationDeviceEntity> deviceList = new ArrayList<>();

        var expectedLocation = locationEntity;
        expectedLocation.setIdentificationDevices(deviceList);

        Mockito.when(locationRepository.findById(5L)).thenThrow(new MockitoException("Location not found"));

        // Act
        // Assert
        Assertions.assertThrows(MockitoException.class, () -> {
            locationService.updateWithDeviceList(5L, deviceList);
        });
    }

    @Test
    public void UpdateWithProduct_ValidLocationId_ValidProductId_ValidProduct_ExpectUpdatedLocation() {
        // Arrange
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(9L);
        productEntity.setNumber("testProductNo");
        productEntity.setName("testProduct");

        var expectedLocation = locationEntity;
        expectedLocation.setProduct(productEntity);

        Mockito.when(locationRepository.findById(5L)).thenReturn(Optional.of(locationEntity));
        Mockito.when(productRepository.existsById(9L)).thenReturn(true);
        Mockito.when(locationRepository.save(any())).thenReturn(expectedLocation);

        // Act
        LocationEntity result = locationService.updateWithProduct(5L, productEntity);

        // Assert
        Mockito.verify(locationRepository, Mockito.times(1)).save(expectedLocation);
        Assertions.assertEquals(expectedLocation, result);
    }

    @Test
    public void UpdateWithProduct_InvalidLocationId_ValidProductId_ValidProduct_ExpectException() {
        // Arrange
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(9L);
        productEntity.setNumber("testProductNo");
        productEntity.setName("testProduct");

        var expectedLocation = locationEntity;
        expectedLocation.setProduct(productEntity);

        Mockito.when(locationRepository.findById(5L)).thenThrow(new MockitoException("Location not found"));

        // Act
        // Assert
        Assertions.assertThrows(MockitoException.class, () -> {
            locationService.updateWithProduct(5L, productEntity);
        });
    }

    @Test
    public void UpdateWithProduct_ValidLocationId_InvalidProductId_ValidProduct_ExpectException() {
        // Arrange
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(9L);
        productEntity.setNumber("testProductNo");
        productEntity.setName("testProduct");

        var expectedLocation = locationEntity;
        expectedLocation.setProduct(productEntity);

        Mockito.when(locationRepository.findById(5L)).thenReturn(Optional.of(locationEntity));
        Mockito.when(productRepository.existsById(9L)).thenThrow(new MockitoException("Product not found"));

        // Act
        // Assert
        Assertions.assertThrows(MockitoException.class, () -> {
            locationService.updateWithProduct(5L, productEntity);
        });
    }

    @Test
    public void Delete_ValidLocationId_ExpectLocationDeleted() {
        // Arrange
        // Act
        locationService.delete(3L);

        // Assert
        Mockito.verify(locationRepository, Mockito.times(1)).deleteById(3L);
    }

}