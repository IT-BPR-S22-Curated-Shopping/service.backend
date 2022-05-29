package bpr.service.backend.services.customerService;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.EventManager;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.DeviceStatusDto;
import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.persistence.repository.deviceRepository.IDeviceRepository;
import bpr.service.backend.services.deviceService.DeviceService;
import bpr.service.backend.util.IDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.beans.PropertyChangeEvent;

@ExtendWith(MockitoExtension.class)
class DeviceServiceStatusUpdateTest {


    @Spy
    private IEventManager eventManager = new EventManager();

    @Mock
    private IDeviceRepository deviceRepository;

    @Mock
    IDateTime dateTime;

    @InjectMocks
    DeviceService deviceService;

    private final long timestamp = 1652463743476L;


    private final IdentificationDeviceEntity repositoryTracker = new IdentificationDeviceEntity(
            "010d2108",
            "bb:27:eb:02:ee:fe",
            "BLE",
            timestamp);

    private IdentificationDeviceEntity identificationDeviceEntity;


    @BeforeEach
    public void beforeEach() {
        identificationDeviceEntity = null;
        eventManager.addListener(Event.DEVICE_OFFLINE, this::setTrackerEntity);
        eventManager.addListener(Event.DEVICE_ONLINE, this::setTrackerEntity);
        eventManager.addListener(Event.DEVICE_READY, this::setTrackerEntity);
        eventManager.addListener(Event.DEVICE_ACTIVE, this::setTrackerEntity);
    }

    private void setFindByIdMock(IdentificationDeviceEntity tracker) {
        Mockito.when(deviceRepository.findByDeviceId(tracker.getDeviceId()))
                .thenReturn(tracker.getDeviceId().equals(repositoryTracker.getDeviceId()) ? repositoryTracker : null);
    }


    private void setTrackerEntity(PropertyChangeEvent event) {
        identificationDeviceEntity = (IdentificationDeviceEntity) event.getNewValue();
    }

    private DeviceStatusDto getStatusDto(String deviceId, String state) {
        long timestamp = 1652463743476L;
        return new DeviceStatusDto(
                timestamp,
                deviceId,
                state
        );
    }

    @Test
    public void knownDeviceAnnounceOnline() {
        // Arrange
        setFindByIdMock(repositoryTracker);

        // Act
        eventManager.invoke(Event.DEVICE_STATUS_UPDATE, getStatusDto(repositoryTracker.getDeviceId(), "ONLINE"));

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(repositoryTracker.getDeviceId());
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_OFFLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(1)).invoke(Event.DEVICE_ONLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_READY, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ACTIVE, repositoryTracker);
        Assertions.assertNotNull(identificationDeviceEntity);
    }

    @Test
    public void knownDeviceAnnounceOnlineLowercase() {
        // Arrange
        setFindByIdMock(repositoryTracker);

        // Act
        eventManager.invoke(Event.DEVICE_STATUS_UPDATE, getStatusDto(repositoryTracker.getDeviceId(), "online"));

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(repositoryTracker.getDeviceId());
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_OFFLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(1)).invoke(Event.DEVICE_ONLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_READY, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ACTIVE, repositoryTracker);
        Assertions.assertNotNull(identificationDeviceEntity);
    }

    @Test
    public void knownDeviceAnnounceOffline() {
        // Arrange
        setFindByIdMock(repositoryTracker);

        // Act
        eventManager.invoke(Event.DEVICE_STATUS_UPDATE, getStatusDto(repositoryTracker.getDeviceId(), "OFFLINE"));

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(repositoryTracker.getDeviceId());
        Mockito.verify(eventManager, Mockito.times(1)).invoke(Event.DEVICE_OFFLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ONLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_READY, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ACTIVE, repositoryTracker);
        Assertions.assertNotNull(identificationDeviceEntity);
    }

    @Test
    public void knownDeviceAnnounceOfflineLowercase() {
        // Arrange
        setFindByIdMock(repositoryTracker);

        // Act
        eventManager.invoke(Event.DEVICE_STATUS_UPDATE, getStatusDto(repositoryTracker.getDeviceId(), "offline"));

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(repositoryTracker.getDeviceId());
        Mockito.verify(eventManager, Mockito.times(1)).invoke(Event.DEVICE_OFFLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ONLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_READY, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ACTIVE, repositoryTracker);
        Assertions.assertNotNull(identificationDeviceEntity);
    }

    @Test
    public void knownDeviceAnnounceReady() {
        // Arrange
        setFindByIdMock(repositoryTracker);

        // Act
        eventManager.invoke(Event.DEVICE_STATUS_UPDATE, getStatusDto(repositoryTracker.getDeviceId(), "READY"));

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(repositoryTracker.getDeviceId());
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_OFFLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ONLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(1)).invoke(Event.DEVICE_READY, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ACTIVE, repositoryTracker);
        Assertions.assertNotNull(identificationDeviceEntity);
    }

    @Test
    public void knownDeviceAnnounceReadyLowercase() {
        // Arrange
        setFindByIdMock(repositoryTracker);

        // Act
        eventManager.invoke(Event.DEVICE_STATUS_UPDATE, getStatusDto(repositoryTracker.getDeviceId(), "ready"));

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(repositoryTracker.getDeviceId());
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_OFFLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ONLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(1)).invoke(Event.DEVICE_READY, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ACTIVE, repositoryTracker);
        Assertions.assertNotNull(identificationDeviceEntity);
    }

    @Test
    public void knownDeviceAnnounceActive() {
        // Arrange
        setFindByIdMock(repositoryTracker);

        // Act
        eventManager.invoke(Event.DEVICE_STATUS_UPDATE, getStatusDto(repositoryTracker.getDeviceId(), "ACTIVE"));

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(repositoryTracker.getDeviceId());
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_OFFLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ONLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_READY, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(1)).invoke(Event.DEVICE_ACTIVE, repositoryTracker);
        Assertions.assertNotNull(identificationDeviceEntity);
    }

    @Test
    public void knownDeviceAnnounceActiveLowercase() {
        // Arrange
        setFindByIdMock(repositoryTracker);

        // Act
        eventManager.invoke(Event.DEVICE_STATUS_UPDATE, getStatusDto(repositoryTracker.getDeviceId(), "active"));

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(repositoryTracker.getDeviceId());
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_OFFLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ONLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_READY, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(1)).invoke(Event.DEVICE_ACTIVE, repositoryTracker);
        Assertions.assertNotNull(identificationDeviceEntity);
    }

    @Test
    public void unknownDeviceState() {
        // Arrange
        setFindByIdMock(repositoryTracker);

        // Act
        eventManager.invoke(Event.DEVICE_STATUS_UPDATE, getStatusDto(repositoryTracker.getDeviceId(), "unknown"));

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(repositoryTracker.getDeviceId());
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_OFFLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ONLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_READY, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ACTIVE, repositoryTracker);
        Assertions.assertNull(identificationDeviceEntity);
    }

    @Test
    public void unknownDeviceAnnouncement() {
        // Arrange
        var unknown = new IdentificationDeviceEntity(
                "010d2108",
                "ff:27:eb:02:ee:ff",
                "BLE",
                timestamp);

        setFindByIdMock(unknown);

        // Act
        eventManager.invoke(Event.DEVICE_STATUS_UPDATE, getStatusDto(unknown.getDeviceId(), "OFFLINE"));

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(unknown.getDeviceId());
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_OFFLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ONLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_READY, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ACTIVE, repositoryTracker);
        Assertions.assertNull(identificationDeviceEntity);
    }
}