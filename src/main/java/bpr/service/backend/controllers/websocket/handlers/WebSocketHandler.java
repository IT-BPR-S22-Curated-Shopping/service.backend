package bpr.service.backend.controllers.websocket.handlers;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public WebSocketHandler(IEventManager eventManager) {
        eventManager.addListener(Event.UUID_DETECTED, this::onMessageReceived);
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
    public void onMessageReceived(PropertyChangeEvent event) {
        for (WebSocketSession session : sessions) {
            session.sendMessage(new TextMessage(event.getNewValue().toString()));
        }
    }
}
