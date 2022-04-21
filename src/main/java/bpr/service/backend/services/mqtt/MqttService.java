package bpr.service.backend.services.mqtt;

import bpr.service.backend.services.IConnectionService;
import bpr.service.backend.services.IConnectionServiceCallback;
import bpr.service.backend.util.ISerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck;
import com.hivemq.client.mqtt.mqtt5.message.unsubscribe.unsuback.Mqtt5UnsubAck;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MqttService implements IConnectionService, IMqttConnection {

    private final Mqtt5AsyncClient client;
    private final String username;
    private final String password;
    private final ISerializer serializer;
    private final Map<String, IConnectionServiceCallback> subscriptions;


    public MqttService(Mqtt5Client client, String username, String password, ISerializer serializer) {
        this.client = (Mqtt5AsyncClient) client;
        this.username = username;
        this.password = password;
        this.serializer = serializer;
        subscriptions = new HashMap<>();
    }


    @Override
    public boolean connect() {
        try {
            var conn = mqttConnect().get();
            return conn != null;
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }

    }

    @Override
    public boolean disconnect() {
        try {
            mqttDisconnect().get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    @Override
    public boolean sendMessage(Object payload) {
        try {
            publish(serializer.toJson(payload));
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("MqttService.sendMessage error: " + e.getMessage());
            return false;
        }
    }


    @Override
    public CompletableFuture<Mqtt5ConnAck> mqttConnect() {
        return client.connectWith()
                .simpleAuth()
                .username(username)
                .password(UTF_8.encode(password))
                .applySimpleAuth()
                .send()
                .whenComplete((ack, throwable) -> {
                    if (throwable != null) {
                        // Handle connection failure
                        System.out.println("MqttService.mqttConnect: Error " + throwable.getMessage());
                    } else {
                        // success
                        System.out.println("MqttService.mqttConnect: success");
                    }

                });
    }

    @Override
    public CompletableFuture<Void> mqttDisconnect() {
        return client
                .disconnect()
                .whenComplete((ack, throwable) -> {
                    if (throwable != null) {
                        // Handle connection failure
                        System.out.println("MqttService.mqttDisconnect: Error " + throwable.getMessage());
                    } else {
                        // success
                        System.out.println("MqttService.mqttDisconnect: success");
                    }
                });
    }

    @Override
    public CompletableFuture<Mqtt5PublishResult> publish(String topic, String payload) {
        if (subscriptions.get(topic) == null || client == null || payload.isBlank()) {
            return null;
        }

        return client.publishWith()
                .topic(topic)
                .payload(UTF_8.encode(payload))
                .send().whenComplete((ack, throwable) -> {
            if (throwable != null) {
                // Handle connection failure
                System.out.println("MqttService.publish: Error " + throwable.getMessage());
            } else {
                // success
                System.out.println("MqttService.publish: success");
            }
        });
    }

    private void publish(String payload) {
        if (payload.isBlank()) return;

        for (String topic : subscriptions.keySet()) {
            publish(topic, payload);
        }
    }

    @Override
    public CompletableFuture<Mqtt5SubAck> subscribe(String topic, IConnectionServiceCallback callback) {
        if (subscriptions.get(topic) != null) return null;

        return client.subscribeWith()
                .topicFilter(topic)
                .callback(cb -> {
                    if (cb.getPayload().isPresent()) {
                        callback.invoke(String.valueOf(UTF_8.decode(cb.getPayload().get())));
                    }
                })
                .send()
                .whenComplete((ack, throwable) -> {
                    if (throwable != null) {
                        // Handle connection failure
                        System.out.println("MqttService.subscribe: Error " + throwable.getMessage());
                    } else {
                        // success
                        subscriptions.put(topic, callback);
                        System.out.println("MqttService.subscribe: success");
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
                        System.out.println("MqttService.unsubscribe: Error " + throwable.getMessage());
                    } else {
                        // success
                        subscriptions.remove(topic);
                        System.out.println("MqttService.unsubscribe: success");
                    }
                });
    }
}