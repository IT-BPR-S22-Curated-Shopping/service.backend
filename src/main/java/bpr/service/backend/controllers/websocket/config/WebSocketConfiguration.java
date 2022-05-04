package bpr.service.backend.controllers.websocket.config;

import bpr.service.backend.controllers.websocket.handlers.WebSocketHandler;
import bpr.service.backend.managers.events.IEventManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final IEventManager eventManager;

    public WebSocketConfiguration(@Autowired @Qualifier("EventManager") IEventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(eventManager), "/presentation").setAllowedOriginPatterns("*");
    }
}
