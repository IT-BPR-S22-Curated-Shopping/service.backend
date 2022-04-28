package bpr.service.backend.services.mqtt;

import bpr.service.backend.MqttMessage;
import bpr.service.backend.services.IConnectionService;
import bpr.service.backend.services.IConnectionServiceCallback;
import bpr.service.backend.util.ISerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component("MqttService")
public class MqttService implements IConnectionService, IMqttConnection {

    private Mqtt5AsyncClient client;
    private final String username;
    private final String password;
    private final ISerializer serializer;
    private final Map<String, IConnectionServiceCallback> subscriptions;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @SneakyThrows
    public MqttService(@Autowired MqttConfiguration configuration, @Autowired @Qualifier("JsonSerializer") ISerializer serializer) {

        this.client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(configuration.getHost())
                .serverPort(configuration.getPort())
                .sslWithDefaultConfig()
                .buildAsync();

        this.username = configuration.getUsername();
        this.password = configuration.getPassword();
        this.serializer = serializer;
        subscriptions = new HashMap<>();
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
    public void sendMessage(String payload) throws Throwable {
        try {
            publish(serializer.toJson(payload));
        } catch (JsonProcessingException e) {
            logger.error("MqttService.sendMessage: " + e.getMessage());
            throw e;
        }
    }


    @Override
    public CompletableFuture<Mqtt5PublishResult> publish(String topic, MqttMessage payload) {
        if (client == null || payload == null) {
            return null;
        }
        logger.debug("Publishing payload:" + payload);
        try {
            return client.publishWith()
                    .topic(topic)
                    .payload(UTF_8.encode(serializer.toJson(payload)))
                    .send()
                    .whenComplete((ack, throwable) -> {
                        if (throwable != null) {
                            logger.error("MqttService.publish: " + throwable.getMessage());
                        }
                    });
        } catch (JsonProcessingException e) {
            logger.error("MqttService.publish: " + e.getMessage());
            return null;
        }

    }

    private void publish(String payload) {
        if (payload.isBlank()) return;

        for (String topic : subscriptions.keySet()) {
            publish(topic, new MqttMessage(payload));
        }
    }

    @Override
    public CompletableFuture<Mqtt5SubAck> subscribe(String topic, IConnectionServiceCallback callback) {
        if (subscriptions.get(topic) != null) return null;

        return client.subscribeWith()
                .topicFilter(topic)
                .callback(cb -> {
                    if (cb.getPayload().isPresent() && callback != null) {
                        callback.onMessageReceived(String.valueOf(UTF_8.decode(cb.getPayload().get())));
                    }
                })
                .send()
                .whenComplete((ack, throwable) -> {
                    if (throwable != null) {
                        // Handle connection failure
                        logger.error("MqttService.subscribe: " + throwable.getMessage());
                    } else {
                        // success
                        subscriptions.put(topic, callback);
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
                        subscriptions.remove(topic);
                        logger.debug("MqttService.unsubscribe: success");
                    }
                });
    }
}