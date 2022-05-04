package bpr.service.backend.services.websocket.handlers;

import bpr.service.backend.controllers.mqtt.MqttService;
import bpr.service.backend.models.mqtt.DeviceModel;
import bpr.service.backend.services.IConnectionServiceCallback;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketHandler extends TextWebSocketHandler implements IConnectionServiceCallback {

    CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public WebSocketHandler(MqttService mqttService) {
        mqttService.subscribe("0462/rpi3/detection", this);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println(session.getPrincipal() + " " + session.getId() + ", " + message.getPayload());
    }

    @SneakyThrows
    @Override
    public void onMessageReceived(DeviceModel payload) {
        for (WebSocketSession session : sessions) {
            session.sendMessage(new TextMessage(payload.getUuid()));
        }
    }
}
