package bpr.service.backend;

import bpr.service.backend.services.ConnectionServiceCallbackImpl;
import bpr.service.backend.services.IConnectionService;
import bpr.service.backend.services.IConnectionServiceCallback;
import bpr.service.backend.services.mqtt.IMqttConnection;
import bpr.service.backend.services.mqtt.MqttService;
import bpr.service.backend.util.JsonSerializer;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        IConnectionService mqtt = new MqttService(MqttClient.builder()
                .useMqttVersion5()
                .serverHost("637b798f8004424f8503cda3e53851b9.s2.eu.hivemq.cloud")
                .serverPort(8883)
                .sslWithDefaultConfig()
                .buildAsync(), "app_backend", "eNZm6SDY8zGkrbJH", new JsonSerializer());
        try {
            mqtt.connect();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        IConnectionServiceCallback callback = new ConnectionServiceCallbackImpl();
        ((IMqttConnection) mqtt).subscribe("someCompany/test", callback);
        ((IMqttConnection) mqtt).publish("someCompany/test", new MqttMessage("Some message"));


        SpringApplication.run(Application.class, args);
    }

}
