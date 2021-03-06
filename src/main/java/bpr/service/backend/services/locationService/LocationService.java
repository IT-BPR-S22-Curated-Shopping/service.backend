package bpr.service.backend.services.locationService;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.IdentifiedCustomerDto;
import bpr.service.backend.models.entities.DetectionSnapshotEntity;
import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.models.entities.LocationEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.persistence.repository.detectionRepository.IDetectionRepository;
import bpr.service.backend.persistence.repository.deviceRepository.IDeviceRepository;
import bpr.service.backend.persistence.repository.locationRepository.ILocationRepository;
import bpr.service.backend.persistence.repository.productRepository.IProductRepository;
import bpr.service.backend.services.locationService.ILocationService;
import bpr.service.backend.util.exceptions.NotFoundException;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service("LocationService")
public class LocationService implements ILocationService {

    private final ILocationRepository locationRepository;
    private final IDeviceRepository deviceRepository;
    private final IProductRepository productRepository;
    private final IDetectionRepository detectionRepository;
    private final IEventManager eventManager;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public LocationService(@Autowired ILocationRepository locationRepository,
                           @Autowired IDeviceRepository deviceRepository,
                           @Autowired IProductRepository productRepository,
                           @Autowired IDetectionRepository detectionRepository,
                           @Autowired @Qualifier("EventManager") IEventManager eventManager) {
        this.locationRepository = locationRepository;
        this.deviceRepository = deviceRepository;
        this.productRepository = productRepository;
        this.detectionRepository = detectionRepository;
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
            activateIdDeviceInLocation(device);
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
            var snapshot = new DetectionSnapshotEntity(
                    identifiedCustomer.getTimestamp(),
                    location.getId(),
                    location.getName(),
                    identifiedCustomer.getIdentificationDeviceId(),
                    identifiedCustomer.getCustomer());
            // If a location does not have a product a recommendation is not needed but data is still valuable.
            if (location.getProduct() == null) {
                logger.info(String.format(
                        "Location service: Product not associated with location %s id %s",
                        location.getName(),
                        location.getId()));
            }
            else {
                logger.info(String.format(
                        "Location service: customer id %s located near product no. %s in location id %s",
                        identifiedCustomer.getCustomer().getId(),
                        location.getProduct().getNumber(),
                        location.getId()));
                snapshot.setProduct(location.getProduct());
                eventManager.invoke(
                        Event.CUSTOMER_LOCATED,
                        snapshot);
            }
            detectionRepository.save(snapshot);
        }
    }



    private boolean inActiveState(IdentificationDeviceEntity idDevice) {
        return idDevice.getTimestampActive() >= idDevice.getTimestampReady() &&
                idDevice.getTimestampActive() >= idDevice.getTimestampOnline() &&
                idDevice.getTimestampActive() > idDevice.getTimestampOffline();
    }

    private boolean inReadyState(IdentificationDeviceEntity idDevice) {
        return idDevice.getTimestampReady() > idDevice.getTimestampActive() &&
                idDevice.getTimestampReady() >= idDevice.getTimestampOnline() &&
                idDevice.getTimestampReady() > idDevice.getTimestampOffline();
    }

    private void deactivateRemovedDevices(List<IdentificationDeviceEntity> oldDeviceEntities, List<IdentificationDeviceEntity> newDeviceEntities) {
        for (var oldEntity : oldDeviceEntities) {
            var removed = true;
            for (var newEntity : newDeviceEntities) {
                if (oldEntity.getDeviceId().equals(newEntity.getDeviceId())) {
                    removed = false;
                    break;
                }
            }
            if (removed && inActiveState(oldEntity))
                deactivateIdDeviceInLocation(oldEntity);
        }
    }

    private void activateNewDevicesInLocation(List<IdentificationDeviceEntity> identificationDeviceEntities) {
        for (var device : identificationDeviceEntities) {
            if (inReadyState(device))
                activateIdDeviceInLocation(device);
        }
    }


    private void activateIdDeviceInLocation(IdentificationDeviceEntity idDevice) {
        eventManager.invoke(Event.ACTIVATE_DEVICE, idDevice);
    }
    private void deactivateIdDeviceInLocation(IdentificationDeviceEntity idDevice) {
        eventManager.invoke(Event.DEACTIVATE_DEVICE, idDevice);
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
    @SneakyThrows
    public LocationEntity updateWithDeviceList(Long id, List<IdentificationDeviceEntity> deviceList) {
        if (locationRepository.findById(id).isPresent()) {
            var databaseLocation = locationRepository.findById(id).get();
            deactivateRemovedDevices(databaseLocation.getIdentificationDevices(), deviceList);

            databaseLocation.setIdentificationDevices(deviceList);

            var updatedEntity = locationRepository.save(databaseLocation);
            activateNewDevicesInLocation(updatedEntity.getIdentificationDevices());

            return updatedEntity;
        }
        throw new NotFoundException(String.format("Location with ID: %s not found", id));
    }

    @Override
    public void delete(Long id) {
        locationRepository.deleteById(id);
    }

    @Override
    public List<LocationEntity> readAll() {
        var locations = new ArrayList<LocationEntity>();
        locationRepository.findAll().forEach(locations::add);
        return locations;
    }

    @Override
    public LocationEntity readById(Long id) {
        return locationRepository.findById(id).orElse(null);
    }

    @Override
    public LocationEntity create(LocationEntity entity) {
        return locationRepository.save(entity);
    }

    @Override
    public LocationEntity createLocation(String name, Long productId, List<String> deviceIds) {
        LocationEntity entity = null;

        if (!name.isEmpty()){
            entity = new LocationEntity();
            entity.setName(name);

            if (productId > 0) {
                var product = productRepository.findById(productId).orElse(null);
                if (product != null) {
                    entity.setProduct(product);
                }
            }

            if (deviceIds.size() > 0) {
                List<IdentificationDeviceEntity> devices = new ArrayList<>();
                for (String deviceId : deviceIds) {
                    var device = deviceRepository.findById(Long.valueOf(deviceId)).orElse(null);
                    if (device != null) {
                        devices.add(device);
                    }
                }
                entity.setIdentificationDevices(devices);
            }
            entity = locationRepository.save(entity);
            logger.info("Created location with: " + entity.toString());
        }
        return entity;
    }

    @Override
    public LocationEntity update(Long id, LocationEntity entity) {
        var databaseLocation = locationRepository.findById(id).get();
        databaseLocation.setProduct(entity.getProduct());
        databaseLocation.setIdentificationDevices(entity.getIdentificationDevices());
        return locationRepository.save(databaseLocation);
    }
}
