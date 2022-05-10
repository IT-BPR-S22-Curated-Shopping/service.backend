package bpr.service.backend.controllers.mqtt;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.data.models.DeviceModel;
import bpr.service.backend.services.IConnectionService;
import bpr.service.backend.util.ISerializer;
import com.hivemq.client.mqtt.MqttClient;
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component("MqttService")
public class MqttConnection implements IConnectionService, IMqttConnection {

    private final IEventManager eventManager;
    private Mqtt5AsyncClient client;
    private final String username;
    private final String password;
    private final ISerializer serializer;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @SneakyThrows
    public MqttConnection(@Autowired MqttConfiguration configuration,
                          @Autowired @Qualifier("JsonSerializer") ISerializer serializer,
                          @Autowired @Qualifier("EventManager") IEventManager eventManager) {

        this.client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(configuration.getHost())
                .serverPort(configuration.getPort())
                .sslWithDefaultConfig()
                .buildAsync();

        this.username = configuration.getUsername();
        this.password = configuration.getPassword();
        this.serializer = serializer;
        this.eventManager = eventManager;
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
                    .send()
                    .whenComplete((act, throwable) -> {
                        if (throwable == null) {
                            logger.info("MqttService.connect: MQTT is connected.");
                        } else {
                            logger.error("MqttService.connect: " + throwable.getMessage());
                        }
                        subscribe("0462/rpi3/detection"); //TODO: Handle this differently
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


    @Override
    public CompletableFuture<Mqtt5PublishResult> publish(String topic, DeviceModel payload) {
        if (client == null || payload == null) {
            return null;
        }
        logger.debug("Publishing payload:" + payload);

        return client.publishWith()
                .topic(topic)
                .payload(UTF_8.encode(serializer.toJson(payload)))
                .send()
                .whenComplete((ack, throwable) -> {
                    if (throwable != null) {
                        logger.error("MqttService.publish: " + throwable.getMessage());
                    }
                });


    }

    @Override
    public CompletableFuture<Mqtt5SubAck> subscribe(String topic) {

        return client.subscribeWith()
                .topicFilter(topic)
                .callback(cb -> {
                    if (cb.getPayload().isPresent()) {
                        eventManager.invoke(Event.UUID_DETECTED, String.valueOf(UTF_8.decode(cb.getPayload().get())));
                    }
                })
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

    @Override
    public CompletableFuture<Mqtt5UnsubAck> unsubscribe(String topic) {
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