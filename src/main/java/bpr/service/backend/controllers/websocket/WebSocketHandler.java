package bpr.service.backend.controllers.websocket;

import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.IdentifiedCustomerDto;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    Map<WebSocketSession, Long> sessionMap = new HashMap<>();

    public WebSocketHandler(@Autowired @Qualifier("EventManager") IEventManager eventManager) {
       // TODO: subscribe to recommendations. eventManager.addListener(Event.NEW_RECOMMENDATION, this::onMessageReceived);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessionMap.put(session, null);
        logger.info("WS Session added: " + session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionMap.remove(session);
        logger.info("WS Session removed: " + session + ", status: " + status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        if (message.getPayload().startsWith("Location ID")) {
            Long locationId = Long.valueOf(message.getPayload().replaceAll("[^0-9]", ""));
            sessionMap.put(session, locationId);
            logger.info(String.valueOf(locationId));
        }
        System.out.println(session.getId() + ", " + message.getPayload());
    }

    @SneakyThrows
    private void sendRecommendation(PropertyChangeEvent event) {

        var idDto = (IdentifiedCustomerDto) event.getNewValue();
        logger.info("onMessageReceived: " + Arrays.toString(Arrays.stream(sessions.stream().toArray()).toArray()));
        if (!sessions.isEmpty()) {
            for (WebSocketSession session : sessions) {
                logger.info("Websocket: sending message received from tracker: " + idDto.getIdentificationDeviceId());
                session.sendMessage(new TextMessage(idDto.getIdentificationDeviceId()));
            }
        }
        else {
            logger.info("Websocket: No active clients connected.");
        }
    }

}
