package bpr.service.backend.managers.events;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@Service("EventManager")
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
    public void invoke(Event event, Object payload) {
        manager.firePropertyChange(event.name(), null, payload);
    }
}
