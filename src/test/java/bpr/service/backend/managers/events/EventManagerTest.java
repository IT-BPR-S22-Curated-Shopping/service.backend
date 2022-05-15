package bpr.service.backend.managers.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;

import static org.junit.jupiter.api.Assertions.*;

class EventManagerTest {

    private IEventManager eventManager;

    private final String invokeString = "Just a test!";
    private String eventString;

    @BeforeEach
    public void beforeEach() {
        eventManager = new EventManager();
        eventString = null;
    }

    private void callback(PropertyChangeEvent event) {
        eventString = (String) event.getNewValue();
    }

    @Test
    public void addListenerInvokeEvent() {
        // Arrange
        eventManager.addListener(Event.MQTT_PUBLISH, this::callback);

        // Act
        eventManager.invoke(Event.MQTT_PUBLISH, invokeString);

        // Assert
        assertEquals(invokeString, eventString);
    }

    @Test
    public void addListenerInvokeOther() {
        // Arrange
        eventManager.addListener(Event.MQTT_PUBLISH, this::callback);

        // Act
        eventManager.invoke(Event.MQTT_SUBSCRIBE, invokeString);

        // Assert
        assertNull(eventString);
    }

}