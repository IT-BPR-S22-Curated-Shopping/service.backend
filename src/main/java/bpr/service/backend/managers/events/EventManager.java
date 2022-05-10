package bpr.service.backend.managers.events;

import org.springframework.stereotype.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@Component("EventManager")
public class EventManager implements IEventManager{
    private final PropertyChangeSupport support;

    public EventManager() {
        support = new PropertyChangeSupport(this);
    }

    @Override
    public void addListener(Event event, PropertyChangeListener listener) {
        support.addPropertyChangeListener(event.name(), listener);
    }

    @Override
    public void removeListener(Event event, PropertyChangeListener listener) {
        support.removePropertyChangeListener(event.name(), listener);
    }

    @Override
    public void invoke(Event event, String payload) {
        support.firePropertyChange(event.name(), null, payload);
    }
}
