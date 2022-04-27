package bpr.service.backend;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


public class MqttMessage implements Serializable {

    @Getter @Setter
    private String rawMessage;

    public MqttMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }
}
