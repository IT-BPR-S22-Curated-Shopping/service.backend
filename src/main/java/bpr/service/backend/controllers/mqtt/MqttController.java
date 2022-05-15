package bpr.service.backend.controllers.mqtt;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.MqttPublishDto;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import java.beans.PropertyChangeEvent;
import java.util.concurrent.ExecutionException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Controller("MqttController")
public class MqttController {

    private final IEventManager eventManager;
    private Mqtt5AsyncClient client;
    private final String username;
    private final String password;
    private final String backendId;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @SneakyThrows
    public MqttController(@Autowired MqttConfiguration configuration,
                          @Autowired @Qualifier("EventManager") IEventManager eventManager) {

        this.client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(configuration.getHost())
                .serverPort(configuration.getPort())
                .sslWithDefaultConfig()
                .buildAsync();

        this.username = configuration.getUsername();
        this.password = configuration.getPassword();
        this.backendId = configuration.getBackendId();
        this.eventManager = eventManager;
        this.eventManager.addListener(Event.MQTT_PUBLISH, this::invokePublish);
        this.eventManager.addListener(Event.MQTT_SUBSCRIBE, this::invokeSubscribe);
        this.eventManager.addListener(Event.MQTT_UNSUBSCRIBE, this::invokeUnsubscribe);
    }

    private void invokeUnsubscribe(PropertyChangeEvent propertyChangeEvent) {
        var topic = (String) propertyChangeEvent.getNewValue();
        unsubscribe(topic);
    }

    private void invokeSubscribe(PropertyChangeEvent propertyChangeEvent) {
        var topic = (String) propertyChangeEvent.getNewValue();
        subscribe(topic);
    }

    private void invokePublish(PropertyChangeEvent propertyChangeEvent) {
        var info = (MqttPublishDto) propertyChangeEvent.getNewValue();
        publish(info.getTopic(), info.getPayload(), MqttQos.AT_MOST_ONCE, false);
    }

    public void setClient(Mqtt5Client client) {
        this.client = (Mqtt5AsyncClient) client;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void connect() throws Throwable {
        try {
            client.connectWith()
                    .simpleAuth()
                    .username(username)
                    .password(UTF_8.encode(password))
                    .applySimpleAuth()
                    .willPublish()
                        .topic(String.format("%s/backend/status" , backendId))
                        .payload("{\"online\":false}".getBytes())
                        .qos(MqttQos.AT_LEAST_ONCE)
                        .retain(true)
                        .applyWillPublish()
                    .send()
                    .whenComplete((act, throwable) -> {
                        if (throwable == null) {
                            logger.info("MqttService.connect: MQTT is connected.");
                        } else {
                            logger.error("MqttService.connect: " + throwable.getMessage());
                        }
                        subscribe(String.format("%s/backend/hello" , backendId));
                        publish(String.format("%s/backend/status" , backendId), "{\"online\":true}", MqttQos.AT_LEAST_ONCE, true);
                    })
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("MqttService.connect: " + e.getMessage());
            throw e;
        }
    }


    public void disconnect() throws Throwable {
        try {
            client
                    .disconnect()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("MqttService.disconnect: " + e.getMessage());
            throw e;
        }
    }

    private void publish(String topic, String payload, MqttQos qos, boolean retain) {
        if (client == null || payload == null) {
            return;
        }
        logger.debug("Publishing payload:" + payload);

        client.publishWith()
                .topic(topic)
                .payload(UTF_8.encode(payload))
                .qos(qos)
                .retain(retain)
                .send()
                .whenComplete((ack, throwable) -> {
                    if (throwable != null) {
                        logger.error("MqttService.publish: " + throwable.getMessage());
                    }
                });
    }


    private void subscribe(String topic) {
        client.subscribeWith()
                .topicFilter(topic)
                .callback(cb -> eventManager.invoke(Event.MQTT_MESSAGE_RECEIVED, cb))
                .send()
                .whenComplete((ack, throwable) -> {
                    if (throwable != null) {
                        // Handle connection failure
                        logger.error("MqttService.subscribe: " + throwable.getMessage());
                    } else {
                        // success
                        //subscriptions.put(topic, callback);
                        logger.debug("MqttService.subscribe: success");
                    }
                });

    }

    private void unsubscribe(String topic) {
        client.unsubscribeWith()
                .topicFilter(topic)
                .send()
                .whenComplete((ack, throwable) -> {
                    if (throwable != null) {
                        // Handle connection failure
                        logger.error("MqttService.unsubscribe: " + throwable.getMessage());
                    } else {
                        // success
                        logger.debug("MqttService.unsubscribe: success");
                    }
                });
    }
}