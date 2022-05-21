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
class MqttMessageServiceStatusTest {

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
    private MqttPublishDto errorDto;
    private DeviceStatusDto statusDto;

    private void setErrorDto(PropertyChangeEvent event) {
        errorDto = (MqttPublishDto) event.getNewValue();
    }

    private void setStatusDto(PropertyChangeEvent event) {
        statusDto = (DeviceStatusDto) event.getNewValue();
    }

    private String getJsonStatus(String state) {
        return String.format("{\"state\":\"%s\"}", state);
    }

    @BeforeEach
    public void beforeEach() {
        eventManager.addListener(Event.MQTT_PUBLISH, this::setErrorDto);
        errorDto = null;
        statusDto = null;
    }

    private void setTopicMock(String json) {
        String companyId = "010d2108";
        Mockito.when(message.getTopic())
                .thenReturn(MqttTopic.of(String.format("%s/%s/status", companyId, deviceId)));
        Optional<ByteBuffer> byteBuf = Optional.of(ByteBuffer.wrap(json.getBytes()));
        Mockito.when(message.getPayload()).thenReturn(byteBuf);
    }

    private void setTimeMock() {
        Mockito.when(dateTime.getEpochMillis()).thenReturn(timestamp);
    }

    @Test
    public void invokeStatusUpdate() {
        // Arrange.
        setTopicMock(getJsonStatus("ONLINE"));
        setTimeMock();
        eventManager.addListener(Event.DEVICE_STATUS_UPDATE, this::setStatusDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Mockito.verify(message, Mockito.times(1)).getTopic();
        Mockito.verify(message, Mockito.times(2)).getPayload();
        Mockito.verify(jsonSerializer, Mockito.times(1)).getJsonNode(Mockito.anyString());
        Mockito.verify(eventManager, Mockito.times(1))
                .invoke(Event.DEVICE_STATUS_UPDATE,
                        new DeviceStatusDto(
                                timestamp,
                                deviceId,
                                statusDto.getState()
                        ));
        Assertions.assertNull(errorDto);
        Assertions.assertNotNull(statusDto);
    }

    @Test
    public void statusUpdateOnlineTrue() {
        // Arrange.
        setTopicMock(getJsonStatus("ONLINE"));
        setTimeMock();
        eventManager.addListener(Event.DEVICE_STATUS_UPDATE, this::setStatusDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(errorDto);
        Assertions.assertEquals("ONLINE", statusDto.getState());
    }

    @Test
    public void statusUpdateDeviceId() {
        // Arrange.
        setTopicMock(getJsonStatus("ONLINE"));
        setTimeMock();
        eventManager.addListener(Event.DEVICE_STATUS_UPDATE, this::setStatusDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(errorDto);
        Assertions.assertEquals(deviceId, statusDto.getDeviceId());
    }

    @Test
    public void statusUpdateStateNotFound() {
        // Arrange.
        var json = "{\"status\":\"ONLINE\"}";
        setTopicMock(json);

        eventManager.addListener(Event.DEVICE_STATUS_UPDATE, this::setStatusDto);
        var expected = "Incorrect status payload. structure. Expected: 'state': 'String'";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.

        Assertions.assertNull(statusDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void statusUpdateStateNotString() {
        // Arrange.
        var json = "{\"state\": true}";
        setTopicMock(json);

        eventManager.addListener(Event.DEVICE_STATUS_UPDATE, this::setStatusDto);
        var expected = "Incorrect type. state must be type string. Options [ OFFLINE, ONLINE, READY, ACTIVE ]";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        String companyId = "010d2108";
        Mockito.verify(eventManager, Mockito.times(1)).invoke(Event.MQTT_PUBLISH, new MqttPublishDto(
                String.format("%s/%s/error", companyId, deviceId),
                expected
        ));
        Assertions.assertNull(statusDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

}