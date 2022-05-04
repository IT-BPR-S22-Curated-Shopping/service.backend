package bpr.service.backend.controllers.mqtt;

import bpr.service.backend.models.mqtt.DeviceModel;
import bpr.service.backend.services.IConnectionServiceCallback;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck;
import com.hivemq.client.mqtt.mqtt5.message.unsubscribe.unsuback.Mqtt5UnsubAck;

import java.util.concurrent.CompletableFuture;

public interface IMqttConnection {
    CompletableFuture<Mqtt5SubAck> subscribe(String topic, IConnectionServiceCallback callback);
    CompletableFuture<Mqtt5UnsubAck> unsubscribe(String topic);
    CompletableFuture<Mqtt5PublishResult> publish(String topic, DeviceModel payload);
}
