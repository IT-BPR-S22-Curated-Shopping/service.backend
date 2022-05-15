package bpr.service.backend.services.mqttMessageService;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.EventManager;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.ConnectedDeviceDto;
import bpr.service.backend.models.dto.ConnectedDeviceErrorDto;
import bpr.service.backend.models.dto.MqttPublishDto;
import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.persistence.repository.deviceRepository.IDeviceRepository;
import bpr.service.backend.util.IDateTime;
import bpr.service.backend.util.ISerializer;
import bpr.service.backend.util.JsonSerializer;
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
import java.time.Instant;

@ExtendWith(MockitoExtension.class)
class MqttMessageServiceCommandTest {

    @Spy
    private IEventManager eventManager = new EventManager();

    @Spy
    private ISerializer jsonSerializer = new JsonSerializer();

    @Mock
    private IDeviceRepository deviceRepository;

    @Mock
    private IDateTime dateTime;

    @InjectMocks
    MqttMessageService messageService;

    private final String companyId = "010d2108";
    private final String deviceId = "b8:27:eb:02:ee:fe";

    private MqttPublishDto publishDto = null;

    @BeforeEach
    public void beforeEach() {
        eventManager.addListener(Event.MQTT_PUBLISH, this::setPublishDto);
        publishDto = null;
    }

    private void setPublishDto(PropertyChangeEvent propertyChangeEvent) {
        publishDto = (MqttPublishDto) propertyChangeEvent.getNewValue();
    }

    @Test
    public void activate() {
        // Arrange
        var idEntity = new IdentificationDeviceEntity(
                companyId,
                deviceId,
                "BLE"
        );

        // ACT
        eventManager.invoke(Event.ACTIVATE_DEVICE, idEntity);

        // ASSERT
        Mockito.verify(eventManager, Mockito.times(1))
                .invoke(Event.MQTT_PUBLISH,
                        new MqttPublishDto(
                                String.format(
                                        "%s/%s/command",
                                        idEntity.getCompanyId(),
                                        idEntity.getDeviceId()),
                                "{\"instruction\":\"ACTIVATE\"}")
                );

        Assertions.assertNotNull(publishDto);
    }

    @Test
    public void deactivate() {
        // Arrange
        var idEntity = new IdentificationDeviceEntity(
                companyId,
                deviceId,
                "BLE"
        );

        // ACT
        eventManager.invoke(Event.DEACTIVATE_DEVICE, idEntity);

        // ASSERT
        Mockito.verify(eventManager, Mockito.times(1))
                .invoke(Event.MQTT_PUBLISH,
                        new MqttPublishDto(
                                String.format(
                                        "%s/%s/command",
                                        idEntity.getCompanyId(),
                                        idEntity.getDeviceId()),
                                "{\"instruction\":\"DEACTIVATE\"}")
                );

        Assertions.assertNotNull(publishDto);
    }
}