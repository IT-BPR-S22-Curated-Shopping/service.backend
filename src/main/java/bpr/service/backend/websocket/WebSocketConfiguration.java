package bpr.service.backend.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry configuration) {
        // websocket prefix for subscriptions
        configuration.enableSimpleBroker("/presentation");
        // websocket prefix for received websocket messages
        configuration.setApplicationDestinationPrefixes("/callback-presentation");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // websocket endpoint
        registry.addEndpoint("/curated-shopping").setAllowedOriginPatterns("*");
        registry.addEndpoint("/curated-shopping").setAllowedOriginPatterns("*").withSockJS();
    }

}
