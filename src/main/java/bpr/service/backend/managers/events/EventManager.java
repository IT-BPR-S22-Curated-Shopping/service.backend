package bpr.service.backend.managers.events;

import bpr.service.backend.data.dto.ConnectedDeviceDto;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import org.springframework.stereotype.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@Component("EventManager")
public class EventManager implements IEventManager{
    private final PropertyChangeSupport manager;

    public EventManager() {
        manager = new PropertyChangeSupport(this);
    }

    @Override
    public void addListener(Event event, PropertyChangeListener listener) {
        manager.addPropertyChangeListener(event.name(), listener);
    }

    @Override
    public void removeListener(Event event, PropertyChangeListener listener) {
        manager.removePropertyChangeListener(event.name(), listener);
    }

    @Override
    public void invoke(Event event, String payload) {
        manager.firePropertyChange(event.name(), null, payload);
    }

    @Override
    public void invoke(Event event, Object payload) {
        manager.firePropertyChange(event.name(), null, payload);
    }
}
