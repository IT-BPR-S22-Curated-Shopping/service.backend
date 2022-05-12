package bpr.service.backend.managers.events;

import java.beans.PropertyChangeListener;

public interface IEventManager {
    void addListener(Event event, PropertyChangeListener listener);
    void removeListener(Event event, PropertyChangeListener listener);
    void invoke(Event event, String payload);
    void invoke(Event event, Object payload);
}
