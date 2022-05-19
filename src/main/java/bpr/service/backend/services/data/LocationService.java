package bpr.service.backend.services.data;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.CustomerLocatedDto;
import bpr.service.backend.models.dto.IdentifiedCustomerDto;
import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.models.entities.LocationEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.persistence.repository.deviceRepository.IDeviceRepository;
import bpr.service.backend.persistence.repository.locationRepository.ILocationRepository;
import bpr.service.backend.persistence.repository.productRepository.IProductRepository;
import bpr.service.backend.util.exceptions.NotFoundException;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;


@Service("LocationService")
public class LocationService implements ICRUDService<LocationEntity> {

    private final ILocationRepository locationRepository;
    private final IDeviceRepository deviceRepository;
    private final IProductRepository productRepository;
    private final IEventManager eventManager;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    public LocationService(@Autowired ILocationRepository locationRepository,
                           @Autowired IDeviceRepository deviceRepository,
                           @Autowired IProductRepository productRepository,
                           @Autowired @Qualifier("EventManager") IEventManager eventManager) {
        this.locationRepository = locationRepository;
        this.deviceRepository = deviceRepository;
        this.productRepository = productRepository;
        this.eventManager = eventManager;
        this.eventManager.addListener(Event.CUSTOMER_IDENTIFIED, this::locateCustomer);
        this.eventManager.addListener(Event.DEVICE_READY, this::activateDevice);
    }

    private void activateDevice(PropertyChangeEvent propertyChangeEvent) {
        var device = (IdentificationDeviceEntity) propertyChangeEvent.getNewValue();
        var location = findLocationByDeviceId(device.getDeviceId());
        if (location == null) {
            logger.info(String.format("Location service: device id %s not associated with any location.", device.getDeviceId()));
        }
        else {
            logger.info(String.format(
                    "Location service: activating device id %s in location %s",
                    device.getDeviceId(),
                    location.getId()));
            eventManager.invoke(Event.ACTIVATE_DEVICE, device);
            //TODO: Save event (key insight).
        }
    }

    private LocationEntity findLocationByDeviceId(String deviceId) {
        var device = deviceRepository.findByDeviceId(deviceId);
        if (device == null) {
            logger.info("Location service: unknown device " + deviceId);
            return null;
        }
        var location = locationRepository.findByIdentificationDevicesIn(List.of(device));
        if(location == null) {
            logger.info("Location service: no location found for device " + deviceId);
        }
        return location;
    }

    private void locateCustomer(PropertyChangeEvent propertyChangeEvent) {
        var identifiedCustomer = (IdentifiedCustomerDto) propertyChangeEvent.getNewValue();
        var location = findLocationByDeviceId(identifiedCustomer.getIdentificationDeviceId());
        if (location != null) {
            if (location.getProduct() == null) {
                logger.info(String.format(
                        "Location service: Product not associated with location %s id %s",
                        location.getName(),
                        location.getId()));
                //TODO: Save event (key insight).
            }
            else if (location.getPresentationDevices() == null || location.getPresentationDevices().isEmpty()) {
                logger.info(String.format(
                        "Location service: Presenter not associated with location %s id %s",
                        location.getName(),
                        location.getId()));
                //TODO: Save event (key insight).
            }
            else {
                logger.info(String.format(
                        "Location service: customer id %s located near product no. %s in location id %s",
                        identifiedCustomer.getCustomer().getId(),
                        location.getProduct().getProductNo(),
                        location.getId()));
                eventManager.invoke(
                        Event.CUSTOMER_LOCATED,
                        new CustomerLocatedDto(
                                identifiedCustomer.getTimestamp(),
                                identifiedCustomer.getCustomer(),
                                location));
            }
        }
    }

    @Override
    public List<LocationEntity> readAll() {
        var locations = new ArrayList<LocationEntity>();
        locationRepository.findAll().forEach(locations::add);
        return locations;
    }

    @Override
    public LocationEntity readById(Long id) {
        if (locationRepository.findById(id).isPresent()) {
            return locationRepository.findById(id).get();
        }
        return null;
    }

    @Override
    public LocationEntity create(LocationEntity entity) {
        return locationRepository.save(entity);
    }

    @Override
    public LocationEntity update(Long id, LocationEntity entity) {
        var databaseLocation = locationRepository.findById(id).get();
        databaseLocation.setProduct(entity.getProduct());
        databaseLocation.setPresentationDevices(entity.getPresentationDevices());
        databaseLocation.setIdentificationDevices(entity.getIdentificationDevices());
        return locationRepository.save(databaseLocation);
    }

    @SneakyThrows
    public LocationEntity updateWithDeviceList(Long id, List<IdentificationDeviceEntity> deviceList) {
        if (locationRepository.findById(id).isPresent()) {
            var databaseLocation = locationRepository.findById(id).get();

            databaseLocation.setIdentificationDevices(deviceList);

            // TODO: Activate devices added to location. Maybe emit event with list before return and then let device service handle the check on their current connection state.

            return locationRepository.save(databaseLocation);
        }
        throw new NotFoundException(String.format("Location with ID: %s not found", id));
    }

    @SneakyThrows
    public LocationEntity updateWithProduct(Long id, ProductEntity product) {
        if (locationRepository.findById(id).isPresent()) {
            if (productRepository.existsById(product.getId())) {
                var databaseLocation = locationRepository.findById(id).get();

                databaseLocation.setProduct(product);

                return locationRepository.save(databaseLocation);
            }
            else throw new NotFoundException(String.format("Product with ID: %s not found", product.getId()));
        }
        throw new NotFoundException(String.format("Location with ID: %s not found", id));
    }

    @Override
    public void delete(Long id) {
        locationRepository.deleteById(id);
    }
}
