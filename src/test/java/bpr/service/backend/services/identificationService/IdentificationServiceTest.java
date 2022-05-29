package bpr.service.backend.services.identificationService;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.EventManager;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.DetectedCustomerDto;
import bpr.service.backend.models.dto.IdentifiedCustomerDto;
import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.models.entities.UuidEntity;
import bpr.service.backend.persistence.repository.customerRepository.ICustomerRepository;
import bpr.service.backend.persistence.repository.customerRepository.IUuidRepository;
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
class IdentificationServiceTest {

    @Spy
    private IEventManager eventManager = new EventManager();

    @Mock
    private ICustomerRepository customerRepository;

    @Mock
    private IUuidRepository idRepository;

    @InjectMocks
    IdentificationService service;

    private UuidEntity repositoryUUID;

    private CustomerEntity repositoryCustomer;

    private IdentifiedCustomerDto customerDto;

    private final long timestamp = 1652463743476L;

    private final String deviceId = "bb:27:eb:02:ee:fe";

    private void setCustomerDto(PropertyChangeEvent event) {
        customerDto = (IdentifiedCustomerDto) event.getNewValue();
    }

    @BeforeEach
    public void beforeEach() {
        repositoryUUID = new UuidEntity("010D2108-0462-4F97-BAB8-000000000002");
        TagEntity repositoryTag = new TagEntity("awesome");
        repositoryCustomer = new CustomerEntity(List.of(repositoryUUID), List.of(repositoryTag));
        customerDto = null;
    }

    @Test
    public void invokeCustomerIdentified() {
        // Arrange
        Mockito.when(idRepository.findByUuid(repositoryUUID.getUuid())).thenReturn(repositoryUUID);
        Mockito.when(customerRepository.findByUuids(repositoryUUID)).thenReturn(repositoryCustomer);

        var detectedDto = new DetectedCustomerDto(timestamp, deviceId, repositoryUUID.getUuid());

        eventManager.addListener(Event.CUSTOMER_IDENTIFIED, this::setCustomerDto);

        // Act
        eventManager.invoke(Event.CUSTOMER_DETECTED, detectedDto);

        // Assert
        Mockito.verify(idRepository, Mockito.times(1)).findByUuid(repositoryUUID.getUuid());
        Mockito.verify(customerRepository, Mockito.times(1)).findByUuids(repositoryUUID);
        Mockito.verify(eventManager, Mockito.times(1))
                .invoke(Event.CUSTOMER_IDENTIFIED, new IdentifiedCustomerDto(
                        timestamp,
                        repositoryCustomer,
                        deviceId
                ));
        Assertions.assertNotNull(customerDto);
    }

    @Test
    public void customerUuidNotFound() {
        // Arrange
        var uuidEntity = new UuidEntity("010D2108-0462-4F97-BAB8-000000000003");

        Mockito.when(idRepository.findByUuid(uuidEntity.getUuid())).thenReturn(null);

        var detectedDto = new DetectedCustomerDto(timestamp, deviceId, uuidEntity.getUuid());

        eventManager.addListener(Event.CUSTOMER_IDENTIFIED, this::setCustomerDto);

        // Act
        eventManager.invoke(Event.CUSTOMER_DETECTED, detectedDto);

        // Assert
        Mockito.verify(idRepository, Mockito.times(1)).findByUuid(uuidEntity.getUuid());
        Mockito.verify(customerRepository, Mockito.times(0)).findByUuids(uuidEntity);
        Mockito.verify(eventManager, Mockito.times(0))
                .invoke(Event.CUSTOMER_IDENTIFIED, new IdentifiedCustomerDto(
                        timestamp,
                        repositoryCustomer,
                        deviceId
                ));
        Assertions.assertNull(customerDto);
    }

    @Test
    public void customerCustomerNotFound() {
        // Arrange
        var uuidEntity = new UuidEntity("010D2108-0462-4F97-BAB8-000000000003");

        Mockito.when(idRepository.findByUuid(uuidEntity.getUuid())).thenReturn(uuidEntity);
        Mockito.when(customerRepository.findByUuids(uuidEntity)).thenReturn(null);

        var detectedDto = new DetectedCustomerDto(timestamp, deviceId, uuidEntity.getUuid());

        eventManager.addListener(Event.CUSTOMER_IDENTIFIED, this::setCustomerDto);

        // Act
        eventManager.invoke(Event.CUSTOMER_DETECTED, detectedDto);

        // Assert
        Mockito.verify(idRepository, Mockito.times(1)).findByUuid(uuidEntity.getUuid());
        Mockito.verify(customerRepository, Mockito.times(1)).findByUuids(uuidEntity);
        Mockito.verify(eventManager, Mockito.times(0))
                .invoke(Event.CUSTOMER_IDENTIFIED, new IdentifiedCustomerDto(
                        timestamp,
                        repositoryCustomer,
                        deviceId
                ));
        Assertions.assertNull(customerDto);
    }
}