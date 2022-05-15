package bpr.service.backend.services.data;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.EventManager;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.ConnectedDeviceDto;
import bpr.service.backend.models.dto.ConnectedDeviceErrorDto;
import bpr.service.backend.models.dto.DeviceStatusDto;
import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.persistence.repository.deviceRepository.IDeviceRepository;
import bpr.service.backend.util.DateTime;
import bpr.service.backend.util.IDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.beans.PropertyChangeEvent;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {


    @Spy
    private IEventManager eventManager = new EventManager();

    @Mock
    private IDeviceRepository deviceRepository;

    @Mock
    IDateTime dateTime;

    @InjectMocks
    DeviceService deviceService;


    private final IdentificationDeviceEntity repositoryTracker = new IdentificationDeviceEntity(
            "010d2108",
            "bb:27:eb:02:ee:fe",
            "BLE");

    private final long timestamp = 1652463743476L;
    private final ConnectedDeviceDto connectionDto = new ConnectedDeviceDto(
            timestamp,
            repositoryTracker.getCompanyId(),
            repositoryTracker.getDeviceId(),
            repositoryTracker.getDeviceType()
    );

    private IdentificationDeviceEntity identificationDeviceEntity;
    private ConnectedDeviceErrorDto errorDto;


    @BeforeEach
    public void beforeEach() {
        identificationDeviceEntity = null;
        errorDto = null;
    }

    private void setFindByIdMock(IdentificationDeviceEntity tracker) {
        Mockito.when(deviceRepository.findByDeviceId(tracker.getDeviceId()))
                .thenReturn(tracker.getDeviceId().equals(repositoryTracker.getDeviceId()) ? repositoryTracker : null);
    }

    private void setTimeMock() {
        Mockito.when(dateTime.getEpochSeconds()).thenReturn(timestamp);
    }

    private void setTrackerEntity(PropertyChangeEvent event) {
        identificationDeviceEntity = (IdentificationDeviceEntity) event.getNewValue();
    }

    private void setErrorDto(PropertyChangeEvent event) {
        errorDto = (ConnectedDeviceErrorDto) event.getNewValue();
    }

    @Test
    public void invokeDeviceConnected() {
        // Arrange
        setFindByIdMock(repositoryTracker);
        eventManager.addListener(Event.INIT_DEVICE_COMM, this::setTrackerEntity);
        eventManager.addListener(Event.DEVICE_CONNECTED_ERROR, this::setErrorDto);

        // Act
        eventManager.invoke(Event.DEVICE_CONNECTED, connectionDto);

        // Assert
        Mockito.verify(eventManager, Mockito.times(1)).invoke(Event.INIT_DEVICE_COMM, repositoryTracker);
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(repositoryTracker.getDeviceId());
        Assertions.assertNull(errorDto);
        Assertions.assertNotNull(identificationDeviceEntity);
    }

    @Test
    public void connectedDeviceCorrectId() {
        // Arrange
        setFindByIdMock(repositoryTracker);
        eventManager.addListener(Event.INIT_DEVICE_COMM, this::setTrackerEntity);
        eventManager.addListener(Event.DEVICE_CONNECTED_ERROR, this::setErrorDto);

        // Act
        eventManager.invoke(Event.DEVICE_CONNECTED, connectionDto);

        // Assert
        Assertions.assertNull(errorDto);
        Assertions.assertEquals(repositoryTracker.getDeviceId(), identificationDeviceEntity.getDeviceId());
    }

    @Test
    public void connectedDeviceCompanyId() {
        // Arrange
        setFindByIdMock(repositoryTracker);
        eventManager.addListener(Event.INIT_DEVICE_COMM, this::setTrackerEntity);
        eventManager.addListener(Event.DEVICE_CONNECTED_ERROR, this::setErrorDto);

        // Act
        eventManager.invoke(Event.DEVICE_CONNECTED, connectionDto);

        // Assert
        Assertions.assertNull(errorDto);
        Assertions.assertEquals(repositoryTracker.getCompanyId(), identificationDeviceEntity.getCompanyId());
    }

    @Test
    public void newConnectedDevice() {
        // Arrange
        var tracker = new IdentificationDeviceEntity(
                "010d2108",
                "ff:27:eb:02:ee:ff",
                "BLE");

        var connectionDto = new ConnectedDeviceDto(timestamp, tracker.getCompanyId(), tracker.getDeviceId(), tracker.getDeviceType());

        setFindByIdMock(tracker);
        Mockito.when(deviceRepository.save(tracker)).thenReturn(tracker);

        eventManager.addListener(Event.INIT_DEVICE_COMM, this::setTrackerEntity);
        eventManager.addListener(Event.DEVICE_CONNECTED_ERROR, this::setErrorDto);

        // Act
        eventManager.invoke(Event.DEVICE_CONNECTED, connectionDto);

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(tracker.getDeviceId());
        Mockito.verify(deviceRepository, Mockito.times(1)).save(tracker);
        Mockito.verify(eventManager, Mockito.times(1)).invoke(Event.INIT_DEVICE_COMM, tracker);
        Assertions.assertNull(errorDto);
        Assertions.assertEquals(tracker.getCompanyId(), identificationDeviceEntity.getCompanyId());
    }

    @Test
    public void existingDeviceIdNewCompanyId() {
        // Arrange
        var tracker = new IdentificationDeviceEntity(
                "010d2109",
                "bb:27:eb:02:ee:fe",
                "BLE");

        var connectionDto = new ConnectedDeviceDto(timestamp, tracker.getCompanyId(), tracker.getDeviceId(), tracker.getDeviceType());

        setFindByIdMock(tracker);
        setTimeMock();

        eventManager.addListener(Event.INIT_DEVICE_COMM, this::setTrackerEntity);
        eventManager.addListener(Event.DEVICE_CONNECTED_ERROR, this::setErrorDto);

        // Act
        eventManager.invoke(Event.DEVICE_CONNECTED, connectionDto);

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(tracker.getDeviceId());
        Mockito.verify(deviceRepository, Mockito.times(0)).save(tracker);
        Mockito.verify(eventManager, Mockito.times(1)).invoke(Event.DEVICE_CONNECTED_ERROR,
                new ConnectedDeviceErrorDto(
                        timestamp,
                        connectionDto,
                        "Device id must be unique."));
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.INIT_DEVICE_COMM, tracker);
        Assertions.assertNull(identificationDeviceEntity);
        Assertions.assertNotNull(errorDto);
    }

    @Test
    public void newConnectedDeviceUnableToCreate() {
        // Arrange
        var tracker = new IdentificationDeviceEntity(
                "010d2108",
                "ff:27:eb:02:ee:ff",
                "BLE");

        var connectionDto = new ConnectedDeviceDto(timestamp, tracker.getCompanyId(), tracker.getDeviceId(), tracker.getDeviceType());

        setFindByIdMock(tracker);
        setTimeMock();
        Mockito.when(deviceRepository.save(tracker)).thenReturn(null);

        eventManager.addListener(Event.INIT_DEVICE_COMM, this::setTrackerEntity);
        eventManager.addListener(Event.DEVICE_CONNECTED_ERROR, this::setErrorDto);

        // Act
        eventManager.invoke(Event.DEVICE_CONNECTED, connectionDto);

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(tracker.getDeviceId());
        Mockito.verify(deviceRepository, Mockito.times(1)).save(tracker);
        Mockito.verify(eventManager, Mockito.times(1)).invoke(Event.DEVICE_CONNECTED_ERROR,
                new ConnectedDeviceErrorDto(
                        timestamp,
                        connectionDto,
                        "Unable to verify connected device."));
        Assertions.assertNull(identificationDeviceEntity);
        Assertions.assertEquals("Unable to verify connected device.", errorDto.getMessage());
    }


    @Test
    public void knownDeviceAnnounceOnline() {
        // Arrange
        setFindByIdMock(repositoryTracker);

        var statusDto = new DeviceStatusDto(
                timestamp,
                repositoryTracker.getDeviceId(),
                "ONLINE"
        );

        eventManager.addListener(Event.DEVICE_ONLINE, this::setTrackerEntity);
        eventManager.addListener(Event.DEVICE_OFFLINE, this::setTrackerEntity);

        // Act
        eventManager.invoke(Event.DEVICE_STATUS_UPDATE, statusDto);

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(repositoryTracker.getDeviceId());
        Mockito.verify(eventManager, Mockito.times(1)).invoke(Event.DEVICE_ONLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_OFFLINE, repositoryTracker);
        Assertions.assertNotNull(identificationDeviceEntity);
    }

    @Test
    public void knownDeviceAnnounceOffline() {
        // Arrange
        setFindByIdMock(repositoryTracker);

        var statusDto = new DeviceStatusDto(
                timestamp,
                repositoryTracker.getDeviceId(),
                "OFFLINE"
        );

        eventManager.addListener(Event.DEVICE_ONLINE, this::setTrackerEntity);
        eventManager.addListener(Event.DEVICE_OFFLINE, this::setTrackerEntity);

        // Act
        eventManager.invoke(Event.DEVICE_STATUS_UPDATE, statusDto);

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(repositoryTracker.getDeviceId());
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ONLINE, repositoryTracker);
        Mockito.verify(eventManager, Mockito.times(1)).invoke(Event.DEVICE_OFFLINE, repositoryTracker);
        Assertions.assertNotNull(identificationDeviceEntity);
    }

    @Test
    public void unknownDeviceAnnouncement() {
        // Arrange
        var unknown = new IdentificationDeviceEntity(
                "010d2108",
                "ff:27:eb:02:ee:ff",
                "BLE");

        var statusDto = new DeviceStatusDto(
                timestamp,
                unknown.getDeviceId(),
                "OFFLINE"
        );
        setFindByIdMock(unknown);

        eventManager.addListener(Event.DEVICE_ONLINE, this::setTrackerEntity);
        eventManager.addListener(Event.DEVICE_OFFLINE, this::setTrackerEntity);

        // Act
        eventManager.invoke(Event.DEVICE_STATUS_UPDATE, statusDto);

        // Assert
        Mockito.verify(deviceRepository, Mockito.times(1)).findByDeviceId(unknown.getDeviceId());
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_ONLINE, unknown);
        Mockito.verify(eventManager, Mockito.times(0)).invoke(Event.DEVICE_OFFLINE, unknown);
        Assertions.assertNull(identificationDeviceEntity);
    }
}