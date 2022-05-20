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
class MqttMessageServiceTelemetryTest {

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
    private final String telemetryLevel = "Info";
    private final String telemetryMsg = "Telemetry Message.";
    private MqttPublishDto errorDto;
    private TelemetryDto telemetryDto;
    private final String json = String.format("{\"level\":\"%s\",\"message\":\"%s\"}", telemetryLevel, telemetryMsg);

    private void setErrorDto(PropertyChangeEvent event) {
        errorDto = (MqttPublishDto) event.getNewValue();
    }

    private void setTelemetryDto(PropertyChangeEvent event) {
        telemetryDto = (TelemetryDto) event.getNewValue();
    }

    @BeforeEach
    public void beforeEach() {
        eventManager.addListener(Event.MQTT_PUBLISH, this::setErrorDto);
        errorDto = null;
        telemetryDto = null;
    }

    private void setTopicMock(String json) {
        String companyId = "010d2108";
        Mockito.when(message.getTopic())
                .thenReturn(MqttTopic.of(String.format("%s/%s/telemetry", companyId, deviceId)));
        Optional<ByteBuffer> byteBuf = Optional.of(ByteBuffer.wrap(json.getBytes()));
        Mockito.when(message.getPayload()).thenReturn(byteBuf);
    }

    private void setTimeMock() {
        Mockito.when(dateTime.getEpochMs()).thenReturn(timestamp);
    }

    @Test
    public void invokeEventTelemetry() {
        // Arrange.
        setTopicMock(json);
        setTimeMock();
        eventManager.addListener(Event.TELEMETRY_RECEIVED, this::setTelemetryDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Mockito.verify(message, Mockito.times(1)).getTopic();
        Mockito.verify(message, Mockito.times(2)).getPayload();
        Mockito.verify(jsonSerializer, Mockito.times(1)).getJsonNode(Mockito.anyString());
        Mockito.verify(eventManager, Mockito.times(1))
                .invoke(Event.TELEMETRY_RECEIVED,
                        new TelemetryDto(
                                timestamp,
                                deviceId,
                                telemetryLevel,
                                telemetryMsg
                        ));
        Assertions.assertNull(errorDto);
        Assertions.assertNotNull(telemetryDto);
    }

    @Test
    public void telemetryLevelCorrect() {
        // Arrange.
        setTopicMock(json);
        eventManager.addListener(Event.TELEMETRY_RECEIVED, this::setTelemetryDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(errorDto);
        Assertions.assertEquals(telemetryLevel, telemetryDto.getLevel());
    }

    @Test
    public void telemetryMsgCorrect() {
        // Arrange.
        setTopicMock(json);
        eventManager.addListener(Event.TELEMETRY_RECEIVED, this::setTelemetryDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(errorDto);
        Assertions.assertEquals(telemetryMsg, telemetryDto.getMessage());
    }

    @Test
    public void telemetryCorrectDeviceId() {
        // Arrange.
        setTopicMock(json);
        eventManager.addListener(Event.TELEMETRY_RECEIVED, this::setTelemetryDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(errorDto);
        Assertions.assertEquals(deviceId, telemetryDto.getDeviceId());
    }

    @Test
    public void telemetryLevelNotString() {
        // Arrange.
        var json = String.format("{\"level\":%s,\"message\":\"%s\"}", 2, telemetryMsg);
        setTopicMock(json);
        eventManager.addListener(Event.TELEMETRY_RECEIVED, this::setTelemetryDto);
        var expected = "Incorrect type. level must be type String";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(telemetryDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void telemetryLevelMissing() {
        // Arrange.
        var json = String.format("{\"message\":\"%s\"}", telemetryMsg);
        setTopicMock(json);
        eventManager.addListener(Event.TELEMETRY_RECEIVED, this::setTelemetryDto);
        var expected = "Incorrect telemetry payload. Expected: 'level': 'messageLevel'";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(telemetryDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void telemetryMsgNotString() {
        // Arrange.
        var json = String.format("{\"level\":\"%s\",\"message\":%s}", telemetryLevel, 2);
        setTopicMock(json);
        eventManager.addListener(Event.TELEMETRY_RECEIVED, this::setTelemetryDto);
        var expected = "Incorrect type. message must be type String";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(telemetryDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void telemetryMsgMissing() {
        // Arrange.
        var json = String.format("{\"level\":\"%s\"}", telemetryLevel);
        setTopicMock(json);
        eventManager.addListener(Event.TELEMETRY_RECEIVED, this::setTelemetryDto);
        var expected = "Incorrect telemetry payload. Expected: 'message': 'String'";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(telemetryDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }
}