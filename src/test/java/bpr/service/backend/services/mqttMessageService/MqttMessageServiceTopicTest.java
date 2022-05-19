package bpr.service.backend.services.mqttMessageService;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.EventManager;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.*;
import bpr.service.backend.persistence.repository.deviceRepository.IDeviceRepository;
import bpr.service.backend.util.IDateTime;
import bpr.service.backend.util.ISerializer;
import bpr.service.backend.util.JsonSerializer;
import com.hivemq.client.mqtt.datatypes.MqttTopic;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
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
import java.nio.ByteBuffer;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MqttMessageServiceTopicTest {

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

    private final Mqtt5Publish message = Mockito.mock(Mqtt5Publish.class, Mockito.RETURNS_DEEP_STUBS);;
    private final String companyId = "010d2108";
    private final String deviceId = "b8:27:eb:02:ee:fe";
    private MqttPublishDto errorDto;
    private DetectedCustomerDto detectedDto;

    private void setErrorDto(PropertyChangeEvent event) {
        errorDto = (MqttPublishDto) event.getNewValue();
    }

    private void setDetectedDto(PropertyChangeEvent event) {
        detectedDto = (DetectedCustomerDto) event.getNewValue();
    }
    

    @BeforeEach
    public void beforeEach() {
        eventManager.addListener(Event.MQTT_PUBLISH, this::setErrorDto);
        errorDto = null;
        detectedDto = null;
    }
    
    @Test
    public void incorrectTopicStructureOneLess() {
        // Arrange.
        Mockito.when(message.getTopic()).thenReturn(MqttTopic.of(String.format("%s/%s", companyId, deviceId)));
        Optional<ByteBuffer> byteBuf = Optional.empty();
        Mockito.when(message.getPayload()).thenReturn(byteBuf);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);
        var expected = "Incorrect topic structure. Expected: companyId/deviceId/channel";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Mockito.verify(message, Mockito.times(2)).getTopic();
        Mockito.verify(message, Mockito.times(0)).getPayload();
        Assertions.assertNull(detectedDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void incorrectTopicStructureOneMore() {
        // Arrange.
        String channelDetection = "detection";
        Mockito.when(message.getTopic()).thenReturn(MqttTopic.of(String.format("%s/%s/%s/error", companyId, deviceId, channelDetection)));
        Optional<ByteBuffer> byteBuf = Optional.empty();
        Mockito.when(message.getPayload()).thenReturn(byteBuf);
        eventManager.addListener(Event.CUSTOMER_IDENTIFIED, this::setDetectedDto);
        var expected = "Incorrect topic structure. Expected: companyId/deviceId/channel";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Mockito.verify(message, Mockito.times(2)).getTopic();
        Mockito.verify(message, Mockito.times(0)).getPayload();
        Assertions.assertNull(detectedDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

}