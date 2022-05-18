package bpr.service.backend.services.data;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.EventManager;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.CustomerLocatedDto;
import bpr.service.backend.models.dto.IdentifiedCustomerDto;
import bpr.service.backend.models.entities.*;
import bpr.service.backend.persistence.repository.deviceRepository.IDeviceRepository;
import bpr.service.backend.persistence.repository.locationRepository.ILocationRepository;
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
import java.util.List;

@ExtendWith(MockitoExtension.class)
class LocationServiceLocateActivateDeviceTest {

    @Spy
    private IEventManager eventManager = new EventManager();

    @Mock
    private IDeviceRepository deviceRepository;

    @Mock
    private ILocationRepository locationRepository;

    @InjectMocks
    LocationService locationService;

    private final long timestamp = 1652463743476L;
    private final String companyId = "010d2108";
    private final String deviceId = "bb:27:eb:02:ee:fe";
    private final TagEntity repositoryTag = new TagEntity("awesome");
    private final UuidEntity repositoryUUID = new UuidEntity("010D2108-0462-4F97-BAB8-000000000002");;

    private final CustomerEntity repositoryCustomer  = new CustomerEntity(List.of(repositoryUUID), List.of(repositoryTag));;

    private final IdentifiedCustomerDto identifiedCustomerDto = new IdentifiedCustomerDto(
            timestamp,
            repositoryCustomer,
            deviceId
    );

    private final IdentificationDeviceEntity device = new IdentificationDeviceEntity(
            companyId,
            deviceId,
            "BLE"
    );

    private final LocationEntity location = new LocationEntity(
            "Prime Beef",
            new ProductEntity(
                    "P02-3627K",
                    "Wagyu"
            ),
            List.of(device),
            List.of(new PresenterEntity())
    );

    private IdentificationDeviceEntity activatedDevice;

    @BeforeEach
    public void beforeEach() {
        eventManager.addListener(Event.ACTIVATE_DEVICE, this::activateDeviceCallBack);
    }

    private void activateDeviceCallBack(PropertyChangeEvent propertyChangeEvent) {
        activatedDevice = (IdentificationDeviceEntity) propertyChangeEvent.getNewValue();
    }

    @Test
    public void canActivateDevice() {
        // Arrange
        Mockito.when(deviceRepository.findByDeviceId(deviceId)
        ).thenReturn(device);
        Mockito.when(locationRepository.findByIdentificationDevicesIn(List.of(device))).thenReturn(location);


        // ACT
        eventManager.invoke(Event.DEVICE_READY, device);

        // ASSERT
        Mockito.verify(eventManager, Mockito.times(1))
                .invoke(Event.ACTIVATE_DEVICE, device);

        Assertions.assertNotNull(activatedDevice);
    }

    @Test
    public void deviceNotFound() {
        // Arrange
        Mockito.when(deviceRepository.findByDeviceId(deviceId)).thenReturn(null);

        // ACT
        eventManager.invoke(Event.DEVICE_READY, device);

        // ASSERT
        Mockito.verify(eventManager, Mockito.times(0))
                .invoke(Event.ACTIVATE_DEVICE, device);

        Assertions.assertNull(activatedDevice);
    }

    @Test
    public void locationNotFound() {
        // Arrange
        Mockito.when(deviceRepository.findByDeviceId(deviceId)).thenReturn(device);
        Mockito.when(locationRepository.findByIdentificationDevicesIn(List.of(device))).thenReturn(null);

        // ACT
        eventManager.invoke(Event.DEVICE_READY, device);

        // ASSERT
        Mockito.verify(eventManager, Mockito.times(0))
                .invoke(Event.ACTIVATE_DEVICE, device);

        Assertions.assertNull(activatedDevice);
    }
}