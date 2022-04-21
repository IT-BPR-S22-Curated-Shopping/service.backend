package bpr.service.backend.services.mqtt;

import bpr.service.backend.services.IConnectionServiceCallback;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck;
import com.hivemq.client.mqtt.mqtt5.message.unsubscribe.unsuback.Mqtt5UnsubAck;

import java.util.concurrent.CompletableFuture;

public interface IMqttConnection {
    CompletableFuture<Mqtt5ConnAck> mqttConnect();
    CompletableFuture<Void> mqttDisconnect();
    CompletableFuture<Mqtt5SubAck> subscribe(String topic, IConnectionServiceCallback callback);
    CompletableFuture<Mqtt5UnsubAck> unsubscribe(String topic);
    CompletableFuture<Mqtt5PublishResult> publish(String topic, String payload);
}
