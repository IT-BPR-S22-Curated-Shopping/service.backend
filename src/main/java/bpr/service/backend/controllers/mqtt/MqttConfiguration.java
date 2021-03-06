package bpr.service.backend.controllers.mqtt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class MqttConfiguration {

    @Getter
    @Value("${service.mqtt.host}")
    private String host;
    @Getter

    @Value("${service.mqtt.port}")
    private int port;
    @Getter
    @Value("${service.mqtt.username}")
    private String username;
    @Getter
    @Value("${service.mqtt.password}")
    private String password;
    
    @Getter
    @Value("${service.mqtt.backendId}")
    private String backendId;


    public MqttConfiguration() {
    }

    public MqttConfiguration(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
}
