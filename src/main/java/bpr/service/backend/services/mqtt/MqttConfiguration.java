package bpr.service.backend.services.mqtt;

import bpr.service.backend.util.ISerializer;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MqttConfiguration {

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
    @Autowired
    @Qualifier("JsonSerializer")
    private ISerializer serializer;

    public MqttConfiguration() {
    }

    public MqttConfiguration(String host, int port, String username, String password, ISerializer serializer) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.serializer = serializer;
    }
}
