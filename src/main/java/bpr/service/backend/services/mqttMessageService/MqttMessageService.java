package bpr.service.backend.services.mqttMessageService;

import bpr.service.backend.models.dto.*;
import bpr.service.backend.models.entities.TrackerEntity;
import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.util.ISerializer;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.beans.PropertyChangeEvent;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component("MqttMessageService")
public class MqttMessageService {

    private final IEventManager eventManager;
    private final ISerializer serializer;

    public MqttMessageService(@Autowired @Qualifier("EventManager") IEventManager eventManager,
                              @Autowired @Qualifier("JsonSerializer")ISerializer serializer) {
        this.eventManager = eventManager;
        this.serializer = serializer;
        eventManager.addListener(Event.MQTT_MESSAGE_RECEIVED, this::MessageHandler);
        eventManager.addListener(Event.DEVICE_INIT_COMM, this::initDeviceCommunication);
        eventManager.addListener(Event.DEVICE_OFFLINE, this::closeDeviceCommunication);
        eventManager.addListener(Event.ACTIVATE_DEVICE, this::ActivateDevice);
        eventManager.addListener(Event.DEACTIVATE_DEVICE, this::DeactivateDevice);
    }

    private void closeDeviceCommunication(PropertyChangeEvent propertyChangeEvent) {
        var device = (TrackerEntity) propertyChangeEvent.getNewValue();
        var root = String.format("%s/%s", device.getCompanyId(), device.getDeviceId());
        eventManager.invoke(Event.MQTT_UNSUBSCRIBE, String.format("%s/detection", root));
        eventManager.invoke(Event.MQTT_UNSUBSCRIBE, String.format("%s/status", root));
        eventManager.invoke(Event.MQTT_UNSUBSCRIBE, String.format("%s/telemetry", root));
    }

    private void initDeviceCommunication(PropertyChangeEvent propertyChangeEvent) {
        var device = (TrackerEntity) propertyChangeEvent.getNewValue();
        var root = String.format("%s/%s", device.getCompanyId(), device.getDeviceId());
        eventManager.invoke(Event.MQTT_SUBSCRIBE, String.format("%s/detection", root));
        eventManager.invoke(Event.MQTT_SUBSCRIBE, String.format("%s/status", root));
        eventManager.invoke(Event.MQTT_SUBSCRIBE, String.format("%s/telemetry", root));
        eventManager.invoke(Event.MQTT_PUBLISH,
                new MqttPublishDto(String.format("%s/%s/command", device.getCompanyId(), device.getDeviceId()), "READY"));
    }

    private void UpdateDeviceState(TrackerEntity device, boolean setActive)
    {
        var state = setActive ? "ACTIVATE" : "DEACTIVATE";
        eventManager.invoke(Event.MQTT_PUBLISH,
                new MqttPublishDto(String.format("%s/%s/command", device.getCompanyId(), device.getDeviceId()), state));
    }

    private void DeactivateDevice(PropertyChangeEvent propertyChangeEvent) {
        var device = (TrackerEntity) propertyChangeEvent.getNewValue();
        UpdateDeviceState(device, false);
    }

    private void ActivateDevice(PropertyChangeEvent propertyChangeEvent) {
        var device = (TrackerEntity) propertyChangeEvent.getNewValue();
        UpdateDeviceState(device, true);
    }

    private void MessageHandler(PropertyChangeEvent event) {
        var message = (Mqtt5Publish) event.getNewValue();
        String[] identifiers = message.getTopic().toString().split("/");
        var company = identifiers[0];
        var device = identifiers[1];
        var topic = identifiers[2];

        switch (topic) {
            case "hello":
                if (message.getPayload().isPresent()) {
                    var messageNode = serializer.getJsonNode(String.valueOf(UTF_8.decode(message.getPayload().get())));
                    var deviceNode = serializer.getJsonNode(messageNode.get("device").toString());

                    var client = new ConnectedDeviceDto(
                            new Date().getTime(),
                            messageNode.get("company").asText(),
                            deviceNode.get("id").asText(),
                            deviceNode.get("type").asText()
                    );
                    eventManager.invoke(Event.DEVICE_CONNECTED, client);
                }
                break;
            case "telemetry":
                if (message.getPayload().isPresent()) {
                    var messageNode = serializer.getJsonNode(String.valueOf(UTF_8.decode(message.getPayload().get())));
                    var telemetry = new TelemetryDto(
                            new Date().getTime(),
                            device,
                            messageNode.get("level").asText(),
                            messageNode.get("message").asText()
                    );
                    eventManager.invoke(Event.TELEMETRY_RECEIVED, telemetry);
                }
                break;
            case "status":
                if(!company.equals("0462") && message.getPayload().isPresent()) {
                    var messageNode = serializer.getJsonNode(String.valueOf(UTF_8.decode(message.getPayload().get())));
                    var status = new DeviceStatusDto(
                            new Date().getTime(),
                            device,
                            messageNode.asText()
                    );
                    eventManager.invoke(Event.DEVICE_STATUS_UPDATE, status);
                }
                break;
            case "detection":
                if (message.getPayload().isPresent()) {
                    var messageNode = serializer.getJsonNode(String.valueOf(UTF_8.decode(message.getPayload().get())));
                    var detection = new DetectedCustomerDto(
                            messageNode.get("timestamp").asLong(),
                            device,
                            messageNode.get("uuid").asText()
                    );
                    eventManager.invoke(Event.CUSTOMER_DETECT, detection);
                }
                break;
            default:
                break;
        }
    }
}