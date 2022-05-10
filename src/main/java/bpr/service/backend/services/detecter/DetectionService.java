package bpr.service.backend.services.detecter;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.DeviceModel;
import bpr.service.backend.persistence.repository.customerRepository.ICustomerRepository;
import bpr.service.backend.util.ISerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


@Service
public class DetectionService implements IDetectionService, PropertyChangeListener {

    private final ICustomerRepository customerRepository;
    private final IEventManager eventManager;
    private final ISerializer serializer;

    public DetectionService(@Autowired ICustomerRepository customerRepository, @Autowired IEventManager eventManager, @Autowired @Qualifier("JsonSerializer")ISerializer serializer) {
        this.customerRepository = customerRepository;
        this.eventManager = eventManager;
        this.serializer = serializer;
        eventManager.addListener(Event.UUID_DETECTED, this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        DeviceModel detection = serializer.fromJsonToDeviceModel(evt.getNewValue().toString());
        if (detection != null) {
            UuidLookup(detection.getUuid());
        }
    }

    private void UuidLookup(String uuid) {
        var customer = customerRepository.findByUuid(uuid);
        if (customer != null) {
            eventManager.invoke(Event.CUSTOMER_DETECT, serializer.toJson(customer));
        }
    }
}
