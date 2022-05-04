package bpr.service.backend.controllers.mqtt;

import bpr.service.backend.managers.events.EventManager;
import bpr.service.backend.services.ConnectionServiceCallbackImpl;
import bpr.service.backend.util.JsonSerializer;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.auth.Mqtt5SimpleAuthBuilder;
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5ConnectBuilder;
import com.hivemq.client.mqtt.mqtt5.message.unsubscribe.Mqtt5UnsubscribeBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


class MqttServiceTest {

    final String host = "localhost";
    final String username = "app_backend";
    final String password = "some password";

    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
    public void canConnectSunny() throws ExecutionException, InterruptedException {
        // Arrange
        var client = Mockito.mock(Mqtt5AsyncClient.class);
        var sendMock = Mockito.mock(Mqtt5ConnectBuilder.Send.class);
        var nestedMock = Mockito.mock(Mqtt5SimpleAuthBuilder.Nested.Complete.class);
        Mockito.when(client.connectWith()).thenReturn(sendMock);
        Mockito.when(sendMock.simpleAuth()).thenReturn(nestedMock);
        Mockito.when(nestedMock.username(username)).thenReturn(nestedMock);
        Mockito.when(nestedMock.password(UTF_8.encode(password))).thenReturn(nestedMock);
        Mockito.when(nestedMock.applySimpleAuth()).thenReturn(sendMock);
        var completedFuture = Mockito.mock(CompletableFuture.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(sendMock.send()).thenReturn(completedFuture);
        Mockito.when(completedFuture.get()).thenReturn("");
        Exception ex = null;
        var conf = new MqttConfiguration(host, 8883, username, password);
        var service = new MqttService(conf, new JsonSerializer(), new EventManager());
        service.setClient(client);

        // act
        try {

            service.connect();
        } catch (Throwable e) {
            ex = (Exception) e;
        }


        // assert
        assertNull(ex);
        Mockito.verify(nestedMock, Mockito.times(1)).username(username);
        Mockito.verify(nestedMock, Mockito.times(1)).password(UTF_8.encode(password));
        Mockito.verify(sendMock, Mockito.times(1)).send();

    }

    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
    public void canConnectRainy() {
        // arrange
        var client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(host)
                .serverPort(8883)
                .sslWithDefaultConfig()
                .buildAsync();
        var conf = new MqttConfiguration(host, 8883, username, password);

        var mqttService = new MqttService(conf, new JsonSerializer(), new EventManager());
        mqttService.setClient(client);
        Exception ex = null;


        // act
        try {
            mqttService.connect();
        } catch (Throwable e) {
            ex = (Exception) e;
        }


        // assert
        assertNotNull(ex);
        System.out.println(ex.getMessage());
    }

    @Test
    public void canDisconnect() {

        // Arrange
        var client = Mockito.mock(Mqtt5AsyncClient.class, Mockito.RETURNS_DEEP_STUBS);
        var conf = new MqttConfiguration(host, 8883, username, password);
        var mqttService = new MqttService(conf, new JsonSerializer(), new EventManager());
        mqttService.setClient(client);
        Exception ex = null;


        // Act
        try {
            mqttService.disconnect();
        } catch (Throwable e) {
            ex = (Exception) e;
        }

        // Assert
        assertNull(ex);
        Mockito.verify(client, Mockito.times(1)).disconnect();
    }


    @Test
    public void canSubscribe() {
        // Arrange
        var topicToSubscribe = "test/topic";
        ConnectionServiceCallbackImpl callback = new ConnectionServiceCallbackImpl();
        var client = Mockito.mock(Mqtt5AsyncClient.class, Mockito.RETURNS_DEEP_STUBS);
        var subMock = Mockito.mock(Mqtt5AsyncClient.Mqtt5SubscribeAndCallbackBuilder.Start.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(client.subscribeWith()).thenReturn(subMock);

        var conf = new MqttConfiguration(host, 8883, username, password);
        var mqttService = new MqttService(conf, new JsonSerializer(), new EventManager());
        mqttService.setClient(client);

        // Act
        mqttService.subscribe(topicToSubscribe);

        // Assert
        Mockito.verify(subMock, Mockito.times(1)).topicFilter(topicToSubscribe);

    }


    @Test
    public void canUnsubscribe() {
        // Arrange
        var topicToSubscribe = "test/topic";

        var client = Mockito.mock(Mqtt5AsyncClient.class, Mockito.RETURNS_DEEP_STUBS);
        var subMock = Mockito.mock((Mqtt5UnsubscribeBuilder.Send.Start.class), Mockito.RETURNS_DEEP_STUBS);
        var completeMock = Mockito.mock(Mqtt5UnsubscribeBuilder.Send.Complete.class);

        Mockito.when(client.unsubscribeWith()).thenReturn(subMock);
        Mockito.when(subMock.topicFilter(topicToSubscribe)).thenReturn(completeMock);
        Mockito.when(completeMock.send()).thenReturn(new CompletableFuture<>());

        var conf = new MqttConfiguration(host, 8883, username, password);
        var mqttService = new MqttService(conf, new JsonSerializer(), new EventManager());
        mqttService.setClient(client);

        // Act
        mqttService.unsubscribe(topicToSubscribe);

        // Assert
        Mockito.verify(subMock, Mockito.times(1)).topicFilter(topicToSubscribe);
        Mockito.verify(completeMock, Mockito.times(1)).send();

    }
}