package bpr.service.backend.controllers.websocket.handlers;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.IdentifiedCustomerDto;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public WebSocketHandler(IEventManager eventManager) {
        eventManager.addListener(Event.CUSTOMER_IDENTIFIED, this::onMessageReceived);
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
        var idDto = (IdentifiedCustomerDto) event.getNewValue();
        if (!sessions.isEmpty()) {
            for (WebSocketSession session : sessions) {
                logger.info("Websocket: sending message received from tracker: " + idDto.getTrackerDeviceId());
                session.sendMessage(new TextMessage(idDto.getTrackerDeviceId()));
            }
        }
        else {
            logger.info("Websocket: No active clients connected.");
        }
    }

}
