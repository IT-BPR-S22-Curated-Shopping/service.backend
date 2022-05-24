package bpr.service.backend.services.data;

import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.persistence.repository.deviceRepository.IDeviceRepository;
import bpr.service.backend.services.deviceService.DeviceService;
import bpr.service.backend.util.DateTime;
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
class DeviceServiceCRUDTest {

    @Mock
    private IDeviceRepository deviceRepository;

    @Mock
    private IEventManager eventManager;

    @Mock
    private DateTime dateTime;

    @InjectMocks
    DeviceService deviceService;

    private IdentificationDeviceEntity identificationDeviceEntity;
    private final long timestamp = 1652463743476L;

    @BeforeEach
    public void beforeEach() {
        identificationDeviceEntity = new IdentificationDeviceEntity(
                "010d2108",
                "bb:27:eb:02:ee:fe",
                "BLE",
                timestamp);
    }
    @Test
    public void ReadAll_AddedDevice_ExpectListWithDevice() {
        // Arrange
        List<IdentificationDeviceEntity> deviceList = new ArrayList<>();
        deviceList.add(identificationDeviceEntity);
        Mockito.when(deviceRepository.findAll()).thenReturn(deviceList);

        // Act
        List<IdentificationDeviceEntity> resultList = deviceService.readAll();

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(2)).findAll();
        Assertions.assertEquals(deviceList, resultList);
    }

    @Test
    public void ReadAll_NoDevices_ExpectEmptyList() {
        // Arrange
        List<IdentificationDeviceEntity> deviceList = new ArrayList<>();
        Mockito.when(deviceRepository.findAll()).thenReturn(deviceList);

        // Act
        List<IdentificationDeviceEntity> resultList = deviceService.readAll();

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(2)).findAll();
        Assertions.assertEquals(deviceList, resultList);
    }

    @Test
    public void ReadById_AddedDevice_ExpectDeviceReturned() {
        // Arrange
        Mockito.when(deviceRepository.findById(1L)).thenReturn(Optional.of(identificationDeviceEntity));

        // Act
        IdentificationDeviceEntity result = deviceService.readById(1L);

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(2)).findById(1L);
        Assertions.assertEquals(identificationDeviceEntity, result);
    }

    @Test
    public void ReadById_NoDeviceWithId_ExpectNullValue() {
        // Arrange
        // Act
        var result = deviceService.readById(1L);
        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findById(1L);
        Assertions.assertNull(result);
    }
}