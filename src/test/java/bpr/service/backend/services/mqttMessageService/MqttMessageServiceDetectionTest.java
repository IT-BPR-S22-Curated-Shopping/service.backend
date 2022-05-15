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
class MqttMessageServiceDetectionTest {

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
    private final String deviceId = "b8:27:eb:02:ee:fe";
    private final Long timestamp = 1652463743476L;
    private final String uuid = "010D2108-0462-4F97-BAB8-000000000002";
    private MqttPublishDto errorDto;
    private DetectedCustomerDto detectedDto;
    private final String json = String.format("{\"timestamp\":%s,\"uuid\":\"%s\"}", timestamp, uuid);

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

    private void setTopicMock(String json) {
        String companyId = "010d2108";
        Mockito.when(message.getTopic())
                .thenReturn(MqttTopic.of(String.format("%s/%s/detection", companyId, deviceId)));
        Optional<ByteBuffer> byteBuf = Optional.of(ByteBuffer.wrap(json.getBytes()));
        Mockito.when(message.getPayload()).thenReturn(byteBuf);
    }

    @Test
    public void invokeEventCustomerDetected() {
        // Arrange.
        setTopicMock(json);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Mockito.verify(message, Mockito.times(1)).getTopic();
        Mockito.verify(message, Mockito.times(2)).getPayload();
        Mockito.verify(jsonSerializer, Mockito.times(1)).getJsonNode(Mockito.anyString());
        Mockito.verify(eventManager, Mockito.times(1))
                .invoke(Event.CUSTOMER_DETECTED,
                        new DetectedCustomerDto(
                                timestamp,
                                deviceId,
                                uuid
                        ));
        Assertions.assertNull(errorDto);
        Assertions.assertNotNull(detectedDto);
    }

    @Test
    public void customerDetectedCorrectTimestamp() {
        // Arrange.
        setTopicMock(json);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.

        Assertions.assertNull(errorDto);
        Assertions.assertEquals(timestamp, detectedDto.getTimestamp());
    }

    @Test
    public void customerDetectedCorrectUuid() {
        // Arrange.
        setTopicMock(json);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(errorDto);
        Assertions.assertEquals(uuid, detectedDto.getUuid());
    }

    @Test
    public void customerDetectedCorrectDeviceId() {
        // Arrange.
        setTopicMock(json);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(errorDto);
        Assertions.assertEquals(deviceId, detectedDto.getDeviceId());
    }

    @Test
    public void customerDetectedUuidNotString() {
        // Arrange.
        var json = String.format("{\"timestamp\":%s,\"uuid\":%S}", timestamp, 15);
        setTopicMock(json);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);
        var expected = "Incorrect type. uuid must be type String";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(detectedDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void customerDetectedTimestampNotLong() {
        // Arrange.
        var json = String.format("{\"timestamp\":\"%s\",\"uuid\":\"%S\"}", timestamp, uuid);
        setTopicMock(json);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);
        var expected = "Incorrect type. timestamp must be type Long";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(detectedDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void customerDetectedNoTimestamp() {
        // Arrange.
        var json = String.format("{\"uuid\":\"%S\"}", uuid);
        setTopicMock(json);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);
        var expected = "Incorrect detection payload. Expected 'timestamp': Long";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(detectedDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void customerDetectedNoUuid() {
        // Arrange.
        var json = String.format("{\"timestamp\":\"%s\"}", timestamp);
        setTopicMock(json);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);
        var expected = "Incorrect detection payload.Expected 'uuid': 'detected uuid'";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(detectedDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }
}