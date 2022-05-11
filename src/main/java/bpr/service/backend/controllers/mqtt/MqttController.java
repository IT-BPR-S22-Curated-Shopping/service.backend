package bpr.service.backend.controllers.mqtt;

import bpr.service.backend.data.dto.MqttPublishDto;
import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.services.IConnectionService;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck;
import com.hivemq.client.mqtt.mqtt5.message.unsubscribe.unsuback.Mqtt5UnsubAck;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.beans.PropertyChangeEvent;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component("MqttService")
public class MqttController implements IConnectionService {

    private final IEventManager eventManager;
    private Mqtt5AsyncClient client;
    private final String username;
    private final String password;

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
        this.eventManager = eventManager;
        this.eventManager.addListener(Event.MQTT_PUBLISH, this::InvokePublish);
        this.eventManager.addListener(Event.MQTT_SUBSCRIBE, this::InvokeSubscribe);
        this.eventManager.addListener(Event.MQTT_UNSUBSCRIBE, this::InvokeUnsubscribe);
    }

    private void InvokeUnsubscribe(PropertyChangeEvent propertyChangeEvent) {
        var topic = (String) propertyChangeEvent.getNewValue();
        unsubscribe(topic);
    }

    private void InvokeSubscribe(PropertyChangeEvent propertyChangeEvent) {
        var topic = (String) propertyChangeEvent.getNewValue();
        subscribe(topic);
    }

    private void InvokePublish(PropertyChangeEvent propertyChangeEvent) {
        var info = (MqttPublishDto) propertyChangeEvent.getNewValue();
        publish(info.getTopic(), info.getPayload(), MqttQos.AT_MOST_ONCE, false);
    }

    public void setClient(Mqtt5Client client) {
        this.client = (Mqtt5AsyncClient) client;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Override
    public void connect() throws Throwable {

        try {
            client.connectWith()
                    .simpleAuth()
                    .username(username)
                    .password(UTF_8.encode(password))
                    .applySimpleAuth()
                    .willPublish()
                        .topic("0462/backend/status")
                        .payload("OFFLINE".getBytes())
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
                        subscribe("0462/backend/hello");
                        publish("0462/backend/status", "ONLINE", MqttQos.AT_LEAST_ONCE, true);
                    })
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("MqttService.connect: " + e.getMessage());
            throw e;
        }
    }

    @Override
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

    private CompletableFuture<Mqtt5PublishResult> publish(String topic, String payload, MqttQos qos, boolean retain) {
        if (client == null || payload == null) {
            return null;
        }
        logger.debug("Publishing payload:" + payload);

        return client.publishWith()
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


    private CompletableFuture<Mqtt5SubAck> subscribe(String topic) {

        return client.subscribeWith()
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

    private CompletableFuture<Mqtt5UnsubAck> unsubscribe(String topic) {
        return client.unsubscribeWith()
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