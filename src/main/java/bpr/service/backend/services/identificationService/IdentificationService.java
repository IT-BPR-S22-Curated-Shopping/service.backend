package bpr.service.backend.services.identificationService;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.DetectedCustomerDto;
import bpr.service.backend.models.dto.IdentifiedCustomerDto;
import bpr.service.backend.persistence.repository.customerRepository.ICustomerRepository;
import bpr.service.backend.persistence.repository.customerRepository.IUuidRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.beans.PropertyChangeEvent;

@Service("IdentificationService")
public class IdentificationService {

    private final ICustomerRepository customerRepository;
    private final IUuidRepository idRepository;
    private final IEventManager eventManager;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public IdentificationService(
            @Autowired @Qualifier("EventManager") IEventManager eventManager,
            @Autowired ICustomerRepository customerRepository,
            @Autowired IUuidRepository idRepository) {
        this.customerRepository = customerRepository;
        this.idRepository = idRepository;
        this.eventManager = eventManager;
        this.eventManager.addListener(Event.CUSTOMER_DETECTED, this::identifyCustomer);
    }

    private void identifyCustomer(PropertyChangeEvent propertyChangeEvent) {
        var detection = (DetectedCustomerDto) propertyChangeEvent.getNewValue();
        var uuid = idRepository.findByUuid(detection.getUuid());
        if (uuid == null) {
            logger.info("Identification Service: UUID not found.");
            return;
        }
        var customer = customerRepository.findByUuids(uuid);
        if (customer == null) {
            logger.info("Identification Service: UUID not found.");
            return;
        }
        logger.info("Identification Service: Identified UUID: " + uuid.getUuid());
        eventManager.invoke(
                Event.CUSTOMER_IDENTIFIED,
                new IdentifiedCustomerDto(
                        detection.getTimestamp(),
                        customer,
                        detection.getDeviceId()));
    }
}