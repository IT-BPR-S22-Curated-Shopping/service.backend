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
class MqttMessageServiceHelloTest {

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
    private final Long timestamp = 1652463743476L;
    private final String deviceType = "BLE";
    private MqttPublishDto errorDto;
    private ConnectedDeviceDto helloDto;
    private final String json = String.format("{\"company\":\"%s\",\"device\":{\"id\":\"%s\",\"type\":\"%s\"}}", companyId, deviceId, deviceType);
    private void setErrorDto(PropertyChangeEvent event) {
        errorDto = (MqttPublishDto) event.getNewValue();
    }
    private void setHelloDto(PropertyChangeEvent event) {
        helloDto = (ConnectedDeviceDto) event.getNewValue();
    }


    @BeforeEach
    public void beforeEach() {
        eventManager.addListener(Event.MQTT_PUBLISH, this::setErrorDto);
        errorDto = null;
        helloDto = null;
    }

    private void setTopicMock(String json) {
        Mockito.when(message.getTopic())
                .thenReturn(MqttTopic.of(String.format("%s/%s/hello", companyId, deviceId)));
        Optional<ByteBuffer> byteBuf = Optional.of(ByteBuffer.wrap(json.getBytes()));
        Mockito.when(message.getPayload()).thenReturn(byteBuf);
    }

    private void setTimeMock() {
        Mockito.when(dateTime.getEpochMs()).thenReturn(timestamp);
    }


    @Test
    public void invokeEventHello() {
        // Arrange.
        setTopicMock(json);
        setTimeMock();
        eventManager.addListener(Event.DEVICE_CONNECTED, this::setHelloDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Mockito.verify(message, Mockito.times(1)).getTopic();
        Mockito.verify(message, Mockito.times(2)).getPayload();
        Mockito.verify(jsonSerializer, Mockito.times(2)).getJsonNode(Mockito.anyString());
        Mockito.verify(eventManager, Mockito.times(1))
                .invoke(Event.DEVICE_CONNECTED,
                        new ConnectedDeviceDto(
                                timestamp,
                                companyId,
                                deviceId,
                                deviceType
                        ));
        Assertions.assertNull(errorDto);
        Assertions.assertNotNull(helloDto);
    }

    @Test
    public void helloCorrectCompanyId() {
        // Arrange.
        setTopicMock(json);
        eventManager.addListener(Event.DEVICE_CONNECTED, this::setHelloDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(errorDto);
        Assertions.assertEquals(companyId, helloDto.getCompanyId());
    }

    @Test
    public void helloCorrectDeviceId() {
        // Arrange.
        setTopicMock(json);
        eventManager.addListener(Event.DEVICE_CONNECTED, this::setHelloDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(errorDto);
        Assertions.assertEquals(deviceId, helloDto.getDeviceId());
    }

    @Test
    public void helloCorrectDeviceType() {
        // Arrange.
        setTopicMock(json);
        eventManager.addListener(Event.DEVICE_CONNECTED, this::setHelloDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(errorDto);
        Assertions.assertEquals(deviceType, helloDto.getDeviceType());
    }

    @Test
    public void helloCompanyMissing() {
        // Arrange.
        var json = String.format("{\"device\":{\"id\":\"%s\",\"type\":\"%s\"}}", deviceId, deviceType);
        setTopicMock(json);
        eventManager.addListener(Event.DEVICE_CONNECTED, this::setHelloDto);
        var expected = "Incorrect hello payload. Expected: 'company': 'companyId'";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(helloDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void helloCompanyNotString() {
        // Arrange.
        var json = String.format("{\"company\":%s,\"device\":{\"id\":\"%s\",\"type\":\"%s\"}}", 5, deviceId, deviceType);
        setTopicMock(json);
        eventManager.addListener(Event.DEVICE_CONNECTED, this::setHelloDto);
        var expected = "Incorrect type. companyId must be type String";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(helloDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void helloDeviceMissing() {
        // Arrange.
        var json = String.format("{\"company\":\"%s\"}", companyId);
        setTopicMock(json);
        eventManager.addListener(Event.DEVICE_CONNECTED, this::setHelloDto);
        var expected = "Incorrect hello payload. Expected: 'device': { 'id': 'deviceId', 'type': 'deviceType' }";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(helloDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void helloDeviceIdNotString() {
        // Arrange.
        var json = String.format("{\"company\":\"%s\",\"device\":{\"id\":%s,\"type\":\"%s\"}}", companyId, 5, deviceType);
        setTopicMock(json);
        eventManager.addListener(Event.DEVICE_CONNECTED, this::setHelloDto);
        var expected = "Incorrect type. deviceId must be type String";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(helloDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void helloDeviceIdMissing() {
        // Arrange.
        var json = String.format("{\"company\":\"%s\",\"device\":{\"type\":\"%s\"}}", companyId, deviceType);
        setTopicMock(json);
        eventManager.addListener(Event.DEVICE_CONNECTED, this::setHelloDto);
        var expected = "Incorrect hello payload. Expected device id: 'device': { 'id': 'deviceId' }";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(helloDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void helloDeviceTypeNotString() {
        // Arrange.
        var json = String.format("{\"company\":\"%s\",\"device\":{\"id\":\"%s\",\"type\":%s}}", companyId, deviceId, 2);
        setTopicMock(json);
        eventManager.addListener(Event.DEVICE_CONNECTED, this::setHelloDto);
        var expected = "Incorrect type. deviceType must be type String";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(helloDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void helloDeviceTypeMissing() {
        // Arrange.
        var json = String.format("{\"company\":\"%s\",\"device\":{\"id\":\"%s\"}}", companyId, deviceId);
        setTopicMock(json);
        eventManager.addListener(Event.DEVICE_CONNECTED, this::setHelloDto);
        var expected = "Incorrect hello payload. Expected device type: 'device': { 'type': 'deviceType' }";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(helloDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }
}