package bpr.service.backend.models.dto;

import lombok.Data;

@Data
public class MqttPublishDto {

    private String topic;
    private String payload;

    public MqttPublishDto() {
    }

    public MqttPublishDto(String topic, String payload) {
        this.topic = topic;
        this.payload = payload;
    }
}
