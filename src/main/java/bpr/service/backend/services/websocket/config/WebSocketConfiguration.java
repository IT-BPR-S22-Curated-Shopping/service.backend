package bpr.service.backend.services.websocket.config;

import bpr.service.backend.services.mqtt.MqttService;
import bpr.service.backend.services.websocket.handlers.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final MqttService mqttService;

    public WebSocketConfiguration(@Autowired MqttService mqttService) {
        this.mqttService = mqttService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(mqttService), "/presentation").setAllowedOriginPatterns("*");
    }
}
