package bpr.service.backend.controllers.websocket;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.PresentationProductDto;
import bpr.service.backend.models.dto.RecommendationDto;
import bpr.service.backend.util.ISerializer;
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
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ISerializer serializer;
    private final Map<Long, List<WebSocketSession>> sessionList;
    private final IEventManager eventManager;


    public WebSocketHandler(@Autowired @Qualifier("EventManager") IEventManager eventManager,
                            @Autowired @Qualifier("JsonSerializer") ISerializer serializer) {
        this.serializer = serializer;
        this.sessionList = new HashMap<>();
        this.eventManager = eventManager;
        eventManager.addListener(Event.NEW_RECOMMENDATION, this::sendRecommendation);
        eventManager.addListener(Event.CURRENT_PRODUCT_RECOMMENDATION, this::sendCurrentProduct);
    }


    private void removeSessionIfExists(WebSocketSession session) {
        var sessionCollection = sessionList.values();
        sessionCollection.forEach(x -> x.removeIf(y -> y.equals(session)));
    }

    private void addSessionIfNotExists(Long id, WebSocketSession session) {
        if (session != null && id > 0) {
            var sessionCollection = sessionList.get(id);
            if (sessionCollection != null) {
                if (!sessionCollection.contains(session)) {
                    sessionCollection.add(session);
                    sessionList.put(id, sessionCollection);
                    logger.info("New connection added to session established on location " + id);
                    invokeInitialProducts(session.getId(), id);
                }
            } else {
                sessionList.put(id, new ArrayList<>(List.of(session)));
                logger.info("New connection added to session established on location " + id);
                invokeInitialProducts(session.getId(), id);
            }


        }
    }

    private void invokeInitialProducts(String sessionId, Long locationId) {
        PresentationProductDto dto = new PresentationProductDto();
        dto.setSessionId(sessionId);
        dto.setLocationId(locationId);
        eventManager.invoke(Event.INIT_RECOMMENDATION, dto);
    }

    private WebSocketSession findSessionWithId(String sessionId) {
        List<WebSocketSession> list = sessionList.values().stream().flatMap(List::stream).collect(Collectors.toList());
        WebSocketSession webSocketSession = null;
        for (WebSocketSession session : list) {
            if (session.getId().equals(sessionId)) {
                webSocketSession = session;
            }
        }
        return webSocketSession;
    }

    @SneakyThrows
    private void sendCurrentProduct(PropertyChangeEvent propertyChangeEvent) {
        var dto = (PresentationProductDto) propertyChangeEvent.getNewValue();
        if (dto != null) {
            var session = findSessionWithId(dto.getSessionId());
            if (session != null) {
                logger.info("Sending initial product.");
                session.sendMessage(new TextMessage(serializer.toJson(dto)));
            } else {
                logger.info("Could not find a session to send initial product information back.");
            }
        } else {
            logger.error("Received event with DTO, but the DTO was empty.");
        }
    }

    @SneakyThrows
    private void sendRecommendation(PropertyChangeEvent event) {
        var recommendation = (RecommendationDto) event.getNewValue();
        if (recommendation != null) {
            var collection = sessionList.get(recommendation.getLocationId());
            if (collection != null) {
                for (WebSocketSession session : collection) {
                    session.sendMessage(new TextMessage(serializer.toJson(recommendation)));
                }
                logger.info("Sending recommendation to location " + recommendation.getLocationId());
            } else {
                logger.info("Websocket: No active clients connected.");
            }
        } else {
            logger.error("Received empty recommendation.");
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        removeSessionIfExists(session);
        logger.info("WS Session removed: " + session + ", status: " + status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        if (message.getPayload().startsWith("Location ID")) {
            long locationId = Long.parseLong(message.getPayload().replaceAll("[^0-9]", ""));
            if (locationId > 0) {
                addSessionIfNotExists(locationId, session);
            } else {
                logger.error("Location id received is invalid. Session not added to list.");
            }
        }
    }
}
