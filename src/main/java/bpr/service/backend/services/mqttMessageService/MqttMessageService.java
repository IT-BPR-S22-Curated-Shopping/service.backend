package bpr.service.backend.services.mqttMessageService;

import bpr.service.backend.models.dto.*;
import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.util.IDateTime;
import bpr.service.backend.util.ISerializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.beans.PropertyChangeEvent;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service("MqttMessageService")
public class MqttMessageService {

    private final IEventManager eventManager;
    private final ISerializer serializer;

    private final IDateTime dateTime;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public MqttMessageService(@Autowired @Qualifier("EventManager") IEventManager eventManager,
                              @Autowired @Qualifier("JsonSerializer")ISerializer serializer,
                              @Autowired @Qualifier("DateTime") IDateTime dateTime) {
        this.eventManager = eventManager;
        this.serializer = serializer;
        this.dateTime = dateTime;
        this.eventManager.addListener(Event.MQTT_MESSAGE_RECEIVED, this::handleMessage);
        this.eventManager.addListener(Event.INIT_DEVICE_COMM, this::initDeviceCommunication);
        this.eventManager.addListener(Event.DEVICE_OFFLINE, this::closeDeviceCommunication);
        this.eventManager.addListener(Event.ACTIVATE_DEVICE, this::activateDevice);
        this.eventManager.addListener(Event.DEACTIVATE_DEVICE, this::deactivateDevice);
        this.eventManager.addListener(Event.DEVICE_CONNECTED_ERROR, this::handleConnectionError);
    }

    private void handleConnectionError(PropertyChangeEvent propertyChangeEvent) {
        var errorDto = (ConnectedDeviceErrorDto) propertyChangeEvent.getNewValue();
        eventManager.invoke(
                Event.MQTT_PUBLISH,
                new MqttPublishDto(
                        String.format(
                                "%s/%s/error",
                                errorDto.getDevice().getCompanyId(),
                                errorDto.getDevice().getDeviceId()),
                        String.format(
                                "{\"payload\": \"%s Connection error: %s\"}",
                                dateTime.convertToDate(errorDto.getTimestamp()),
                                errorDto.getMessage())
                ));
    }

    private void closeDeviceCommunication(PropertyChangeEvent propertyChangeEvent) {
        var device = (IdentificationDeviceEntity) propertyChangeEvent.getNewValue();
        var root = String.format("%s/%s", device.getCompanyId(), device.getDeviceId());
        logger.info(String.format("MQTT message service: unsubscribing from all topics related to %s", root));
        eventManager.invoke(Event.MQTT_UNSUBSCRIBE, String.format("%s/detection", root));
        eventManager.invoke(Event.MQTT_UNSUBSCRIBE, String.format("%s/status", root));
        eventManager.invoke(Event.MQTT_UNSUBSCRIBE, String.format("%s/telemetry", root));
    }

    private void initDeviceCommunication(PropertyChangeEvent propertyChangeEvent) {
        var device = (IdentificationDeviceEntity) propertyChangeEvent.getNewValue();
        logger.info("MQTT Message Service: Subscribing to " + device.getDeviceId());
        logger.info("MQTT Message Service: Subscribing to " + device.getDeviceId());
        var root = String.format("%s/%s", device.getCompanyId(), device.getDeviceId());
        logger.info(String.format("MQTT message service: subscribing to all topics related to %s", root));
        eventManager.invoke(Event.MQTT_SUBSCRIBE, String.format("%s/detection", root));
        eventManager.invoke(Event.MQTT_SUBSCRIBE, String.format("%s/status", root));
        eventManager.invoke(Event.MQTT_SUBSCRIBE, String.format("%s/telemetry", root));
        eventManager.invoke(Event.MQTT_PUBLISH,
                new MqttPublishDto(String.format("%s/command", root), "{\"instruction\":\"READY\"}"));
    }

    private void updateDeviceState(IdentificationDeviceEntity device, boolean setActive)
    {
        var state = setActive ? "{\"instruction\":\"ACTIVATE\"}" : "{\"instruction\":\"DEACTIVATE\"}";
        eventManager.invoke(Event.MQTT_PUBLISH,
                new MqttPublishDto(String.format("%s/%s/command", device.getCompanyId(), device.getDeviceId()), state));
    }

    private void deactivateDevice(PropertyChangeEvent propertyChangeEvent) {
        var device = (IdentificationDeviceEntity) propertyChangeEvent.getNewValue();
        updateDeviceState(device, false);
    }

    private void activateDevice(PropertyChangeEvent propertyChangeEvent) {
        var device = (IdentificationDeviceEntity) propertyChangeEvent.getNewValue();
        updateDeviceState(device, true);
    }

    private void invokeMqttError(String topic, String message) {
        logger.warn(String.format("MQTT received incorrect request. Message: %s returned to: %s", message, topic));
        eventManager.invoke(
                Event.MQTT_PUBLISH,
                new MqttPublishDto(
                        topic,
                        message
                )
        );
    }

    private void handleHello(JsonNode messageNode, String errorTopic) {
        if (!messageNode.has("device")) {
            invokeMqttError(errorTopic,
                    "Incorrect hello payload. Expected: 'device': { 'id': 'deviceId', 'type': 'deviceType' }");
            return;
        }
        if (!messageNode.has("company")) {
            invokeMqttError(errorTopic,
                    "Incorrect hello payload. Expected: 'company': 'companyId'");
            return;
        }

        var deviceNode = serializer.getJsonNode(messageNode.get("device").toString());

        if (!deviceNode.has("id")) {
            invokeMqttError(errorTopic,
                    "Incorrect hello payload. Expected device id: 'device': { 'id': 'deviceId' }");
            return;
        }
        if (!deviceNode.has("type")) {
            invokeMqttError(errorTopic,
                    "Incorrect hello payload. Expected device type: 'device': { 'type': 'deviceType' }");
            return;
        }

        var companyId = messageNode.get("company");
        var deviceId = deviceNode.get("id");
        var deviceType = deviceNode.get("type");

        if (!companyId.isTextual()) {
            invokeMqttError(errorTopic,
                    "Incorrect type. companyId must be type String");
            return;
        }
        if (!deviceId.isTextual()) {
            invokeMqttError(errorTopic,
                    "Incorrect type. deviceId must be type String");
            return;
        }
        if (!deviceType.isTextual()) {
            invokeMqttError(errorTopic,
                    "Incorrect type. deviceType must be type String");
            return;
        }

        var client = new ConnectedDeviceDto(
                dateTime.getEpochMs(),
                companyId.asText(),
                deviceId.asText(),
                deviceType.asText()
        );
        eventManager.invoke(Event.DEVICE_CONNECTED, client);
    }

    private void handleTelemetry(String deviceId, JsonNode messageNode, String errorTopic) {
        if (!messageNode.has("level")){
            invokeMqttError(errorTopic,
                    "Incorrect telemetry payload. Expected: 'level': 'messageLevel'");
            return;
        }
        if (!messageNode.has("message")){
            invokeMqttError(errorTopic,
                    "Incorrect telemetry payload. Expected: 'message': 'String'");
            return;
        }
        var level = messageNode.get("level");
        var message = messageNode.get("message");

        if (!level.isTextual()) {
            invokeMqttError(errorTopic,
                    "Incorrect type. level must be type String");
            return;
        }
        if (!message.isTextual()) {
            invokeMqttError(errorTopic,
                    "Incorrect type. message must be type String");
            return;
        }

        var telemetry = new TelemetryDto(
                dateTime.getEpochMs(),
                deviceId,
                level.asText(),
                message.asText()
        );
        eventManager.invoke(Event.TELEMETRY_RECEIVED, telemetry);
    }

    private void handleStatusUpdate(String deviceId, JsonNode messageNode, String errorTopic) {
        if (!messageNode.has("state")) {
            invokeMqttError(errorTopic,
                    "Incorrect status payload. structure. Expected: 'state': 'String'");
            return;
        }
        var state = messageNode.get("state");

        if (!state.isTextual()) {
            invokeMqttError(errorTopic,
                    "Incorrect type. state must be type string. Options [ OFFLINE, ONLINE, READY, ACTIVE ]");
            return;
        }

        var status = new DeviceStatusDto(
                dateTime.getEpochMs(),
                deviceId,
                state.asText()
        );
        eventManager.invoke(Event.DEVICE_STATUS_UPDATE, status);
    }

    private void handleDetection(String deviceId, JsonNode messageNode, String errorTopic) {
        if (!messageNode.has("timestamp")) {
            invokeMqttError(errorTopic,
                    "Incorrect detection payload. Expected 'timestamp': Long");
            return;
        }
        if (!messageNode.has("uuid")) {
            invokeMqttError(errorTopic,
                    "Incorrect detection payload.Expected 'uuid': 'detected uuid'");
            return;
        }
        var timestamp = messageNode.get("timestamp");
        var uuid = messageNode.get("uuid");

        if (!timestamp.isLong()) {
            invokeMqttError(errorTopic,
                    "Incorrect type. timestamp must be type Long");
            return;
        }
        if (!uuid.isTextual()) {
            invokeMqttError(errorTopic,
                    "Incorrect type. uuid must be type String");
            return;
        }

        var detection = new DetectedCustomerDto(
                timestamp.asLong(),
                deviceId,
                uuid.asText()
        );
        eventManager.invoke(Event.CUSTOMER_DETECTED, detection);
    }

    private void handleMessage(PropertyChangeEvent event) {
        var message = (Mqtt5Publish) event.getNewValue();

        String[] topicParts = message.getTopic().toString().split("/");
        if (topicParts.length != 3) {
            invokeMqttError(message.getTopic() + "/error",
                    "Incorrect topic structure. Expected: companyId/deviceId/channel");
            return;
        }
        var companyId = topicParts[0];
        var deviceId = topicParts[1];
        var channel = topicParts[2];

        var errorTopic = String.format("%s/%s/error", companyId, deviceId);

        if (message.getPayload().isEmpty()) {
            invokeMqttError(errorTopic,
                    String.format("Payload not found in channel: %s. Cannot be empty!", channel));
            return;
        }

        var messageNode = serializer.getJsonNode(String.valueOf(UTF_8.decode(message.getPayload().get())));

        switch (channel) {
            case "hello":
                handleHello(messageNode, errorTopic);
                break;
            case "telemetry":
                handleTelemetry(deviceId, messageNode, errorTopic);
                break;
            case "status":
                if(!companyId.equals("0462")) {
                    handleStatusUpdate(deviceId, messageNode, errorTopic);
                }
                break;
            case "detection":
                handleDetection(deviceId, messageNode, errorTopic);
                break;
            default:
                invokeMqttError(errorTopic,
                        "Unknown channel. Options: [ hello, status, telemetry, detection ]");
                break;
        }
    }
}