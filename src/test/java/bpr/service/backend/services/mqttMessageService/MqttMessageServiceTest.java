package bpr.service.backend.services.mqttMessageService;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.EventManager;
import bpr.service.backend.models.dto.*;
import bpr.service.backend.util.JsonSerializer;
import com.hivemq.client.mqtt.datatypes.MqttTopic;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.beans.PropertyChangeEvent;
import java.nio.ByteBuffer;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;

class MqttMessageServiceTest {

    private EventManager eventManager;
    private Mqtt5Publish message;

    private final String channelHello = "hello";
    private final String channelTelemetry = "telemetry";
    private final String channelStatus = "status";
    private final String channelDetection = "detection";
    private final String companyId = "010d2108";
    private final String deviceId = "b8:27:eb:02:ee:fe";
    private final Long timestamp = 1652463743476L;
    private final String uuid = "010D2108-0462-4F97-BAB8-000000000002";
    private final String telemetryLevel = "Info";
    private final String telemetryMsg = "Info";
    private final String deviceType = "BLE";
    private MqttPublishDto errorDto;
    private DetectedCustomerDto detectedDto;
    private TelemetryDto telemetryDto;
    private DeviceStatusDto statusDto;
    private ConnectedDeviceDto helloDto;
    private final String jsonDetected = String.format("{\"timestamp\":%s,\"uuid\":\"%s\"}", timestamp, uuid);
    private final String jsonStatus = String.format("{\"online\":%s}", true);
    private final String jsonTelemetry = String.format("{\"level\":\"%s\",\"message\":\"%s\"}", telemetryLevel, telemetryMsg);
    private final String jsonHello = String.format("{\"company\":\"%s\",\"device\":{\"id\":\"%s\",\"type\":\"%s\"}}", companyId, deviceId, deviceType);

    @BeforeEach
    public void beforeEach() {
        message = Mockito.mock(Mqtt5Publish.class, Mockito.RETURNS_DEEP_STUBS);
        errorDto = null;
        detectedDto = null;
        statusDto = null;
        telemetryDto = null;
        helloDto = null;
        eventManager = new EventManager();
        new MqttMessageService(eventManager, new JsonSerializer());
        eventManager.addListener(Event.MQTT_PUBLISH, this::setErrorDto);
    }

    private void setErrorDto(PropertyChangeEvent event) {
        errorDto = (MqttPublishDto) event.getNewValue();
    }

    private void setDetectedDto(PropertyChangeEvent event) {
        detectedDto = (DetectedCustomerDto) event.getNewValue();
    }

    private void setStatusDto(PropertyChangeEvent event) {
        statusDto = (DeviceStatusDto) event.getNewValue();
    }

    private void setTelemetryDto(PropertyChangeEvent event) {
        telemetryDto = (TelemetryDto) event.getNewValue();
    }

    private void setHelloDto(PropertyChangeEvent event) {
        helloDto = (ConnectedDeviceDto) event.getNewValue();
    }

    private void setMock(String channel, String json) {
        Mockito.when(message.getTopic())
                .thenReturn(MqttTopic.of(String.format("%s/%s/%s", companyId, deviceId, channel)));
        Optional<ByteBuffer> byteBuf = Optional.of(ByteBuffer.wrap(json.getBytes()));
        Mockito.when(message.getPayload()).thenReturn(byteBuf);
    }

    @Test
    public void emptyPayload() {
        // Arrange.
        Mockito.when(message.getTopic()).thenReturn(MqttTopic.of(String.format("%s/%s/%s", companyId, deviceId, channelDetection)));
        Optional<ByteBuffer> byteBuf = Optional.empty();
        Mockito.when(message.getPayload()).thenReturn(byteBuf);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(detectedDto);
        Assertions.assertNotNull(errorDto);
    }

    @Test
    public void emptyPayloadContent() {
        // Arrange.
        Mockito.when(message.getTopic()).thenReturn(MqttTopic.of(String.format("%s/%s/%s", companyId, deviceId, channelDetection)));
        Optional<ByteBuffer> byteBuf = Optional.empty();
        Mockito.when(message.getPayload()).thenReturn(byteBuf);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);
        var expected = String.format("Payload not found in channel: %s. Cannot be empty!", channelDetection);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertEquals(expected, errorDto.getPayload());
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
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void incorrectTopicStructureOneMore() {
        // Arrange.
        Mockito.when(message.getTopic()).thenReturn(MqttTopic.of(String.format("%s/%s/%s/error", companyId, deviceId, channelDetection)));
        Optional<ByteBuffer> byteBuf = Optional.empty();
        Mockito.when(message.getPayload()).thenReturn(byteBuf);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);
        var expected = "Incorrect topic structure. Expected: companyId/deviceId/channel";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void invokeEventCustomerDetected() {
        // Arrange.
        setMock(channelDetection, jsonDetected);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(errorDto);
        Assertions.assertNotNull(detectedDto);
    }

    @Test
    public void customerDetectedCorrectTimestamp() {
        // Arrange.
        setMock(channelDetection, jsonDetected);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertEquals(timestamp, detectedDto.getTimestamp());
    }

    @Test
    public void customerDetectedCorrectUuid() {
        // Arrange.
        setMock(channelDetection, jsonDetected);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertEquals(uuid, detectedDto.getUuid());
    }

    @Test
    public void customerDetectedCorrectDeviceId() {
        // Arrange.
        setMock(channelDetection, jsonDetected);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertEquals(deviceId, detectedDto.getDeviceId());
    }

    @Test
    public void customerDetectedUuidNotString() {
        // Arrange.
        var json = String.format("{\"timestamp\":%s,\"uuid\":%S}", timestamp, 15);
        setMock(channelDetection, json);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);
        var expected = "Incorrect type. uuid must be type String";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(detectedDto);
        Assertions.assertNotNull(errorDto);

        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void customerDetectedTimestampNotLong() {
        // Arrange.
        var json = String.format("{\"timestamp\":\"%s\",\"uuid\":\"%S\"}", timestamp, uuid);
        setMock(channelDetection, json);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);
        var expected = "Incorrect type. timestamp must be type Long";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(detectedDto);
        Assertions.assertNotNull(errorDto);

        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void customerDetectedNoTimestamp() {
        // Arrange.
        var json = String.format("{\"uuid\":\"%S\"}", uuid);
        setMock(channelDetection, json);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);
        var expected = "Incorrect detection payload. Expected 'timestamp': Long";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(detectedDto);
        Assertions.assertNotNull(errorDto);

        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void customerDetectedNoUuid() {
        // Arrange.
        var json = String.format("{\"timestamp\":\"%s\"}", timestamp);
        setMock(channelDetection, json);
        eventManager.addListener(Event.CUSTOMER_DETECTED, this::setDetectedDto);
        var expected = "Incorrect detection payload.Expected 'uuid': 'detected uuid'";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(detectedDto);
        Assertions.assertNotNull(errorDto);

        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void invokeStatusUpdate() {
        // Arrange.
        setMock(channelStatus, jsonStatus);
        eventManager.addListener(Event.DEVICE_STATUS_UPDATE, this::setStatusDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(errorDto);
        Assertions.assertNotNull(statusDto);
    }

    @Test
    public void statusUpdateOnlineTrue() {
        // Arrange.
        setMock(channelStatus, jsonStatus);
        eventManager.addListener(Event.DEVICE_STATUS_UPDATE, this::setStatusDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertEquals(true, statusDto.isOnline());
    }

    @Test
    public void statusUpdateDeviceId() {
        // Arrange.
        setMock(channelStatus, jsonStatus);
        eventManager.addListener(Event.DEVICE_STATUS_UPDATE, this::setStatusDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertEquals(deviceId, statusDto.getDeviceId());
    }

    @Test
    public void statusUpdateOnlineNotFound() {
        // Arrange.
        var json = String.format("{\"offline\":%s}", true);
        setMock(channelStatus, json);
        eventManager.addListener(Event.DEVICE_STATUS_UPDATE, this::setStatusDto);
        var expected = "Incorrect status payload. structure. Expected: 'online': boolean";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(statusDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void statusUpdateOnlineNotBoolean() {
        // Arrange.
        var json = String.format("{\"online\":\"%s\"}", true);
        setMock(channelStatus, json);
        eventManager.addListener(Event.DEVICE_STATUS_UPDATE, this::setStatusDto);
        var expected = "Incorrect type. online must be type boolean";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(statusDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void invokeEventTelemetry() {
        // Arrange.
        setMock(channelTelemetry, jsonTelemetry);
        eventManager.addListener(Event.TELEMETRY_RECEIVED, this::setTelemetryDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(errorDto);
        Assertions.assertNotNull(telemetryDto);
    }

    @Test
    public void telemetryLevelCorrect() {
        // Arrange.
        setMock(channelTelemetry, jsonTelemetry);
        eventManager.addListener(Event.TELEMETRY_RECEIVED, this::setTelemetryDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertEquals(telemetryLevel, telemetryDto.getLevel());
    }

    @Test
    public void telemetryMsgCorrect() {
        // Arrange.
        setMock(channelTelemetry, jsonTelemetry);
        eventManager.addListener(Event.TELEMETRY_RECEIVED, this::setTelemetryDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertEquals(telemetryMsg, telemetryDto.getMessage());
    }

    @Test
    public void telemetryCorrectDeviceId() {
        // Arrange.
        setMock(channelTelemetry, jsonTelemetry);
        eventManager.addListener(Event.TELEMETRY_RECEIVED, this::setTelemetryDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertEquals(deviceId, telemetryDto.getDeviceId());
    }

    @Test
    public void telemetryLevelNotString() {
        // Arrange.
        var json = String.format("{\"level\":%s,\"message\":\"%s\"}", 2, telemetryMsg);
        setMock(channelTelemetry, json);
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
        setMock(channelTelemetry, json);
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
        setMock(channelTelemetry, json);
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
        setMock(channelTelemetry, json);
        eventManager.addListener(Event.TELEMETRY_RECEIVED, this::setTelemetryDto);
        var expected = "Incorrect telemetry payload. Expected: 'message': 'String'";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(telemetryDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }

    @Test
    public void invokeEventHello() {
        // Arrange.
        setMock(channelHello, jsonHello);
        eventManager.addListener(Event.DEVICE_CONNECTED, this::setHelloDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(errorDto);
        Assertions.assertNotNull(helloDto);
    }

    @Test
    public void helloCorrectCompanyId() {
        // Arrange.
        setMock(channelHello, jsonHello);
        eventManager.addListener(Event.DEVICE_CONNECTED, this::setHelloDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertEquals(companyId, helloDto.getCompanyId());
    }

    @Test
    public void helloCorrectDeviceId() {
        // Arrange.
        setMock(channelHello, jsonHello);
        eventManager.addListener(Event.DEVICE_CONNECTED, this::setHelloDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertEquals(deviceId, helloDto.getDeviceId());
    }

    @Test
    public void helloCorrectDeviceType() {
        // Arrange.
        setMock(channelHello, jsonHello);
        eventManager.addListener(Event.DEVICE_CONNECTED, this::setHelloDto);

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertEquals(deviceType, helloDto.getDeviceType());
    }

    @Test
    public void helloCompanyMissing() {
        // Arrange.
        var json = String.format("{\"device\":{\"id\":\"%s\",\"type\":\"%s\"}}", deviceId, deviceType);
        setMock(channelHello, json);
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
        setMock(channelHello, json);
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
        setMock(channelHello, json);
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
        setMock(channelHello, json);
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
        setMock(channelHello, json);
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
        setMock(channelHello, json);
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
        setMock(channelHello, json);
        eventManager.addListener(Event.DEVICE_CONNECTED, this::setHelloDto);
        var expected = "Incorrect hello payload. Expected device type: 'device': { 'type': 'deviceType' }";

        // Act.
        eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, message);

        // Assert.
        Assertions.assertNull(helloDto);
        Assertions.assertEquals(expected, errorDto.getPayload());
    }
}