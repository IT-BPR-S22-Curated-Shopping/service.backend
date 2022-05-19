package bpr.service.backend.services.data;

import bpr.service.backend.models.dto.ConnectedDeviceDto;
import bpr.service.backend.models.dto.ConnectedDeviceErrorDto;
import bpr.service.backend.models.dto.DeviceStatusDto;
import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.persistence.repository.deviceRepository.IDeviceRepository;
import bpr.service.backend.util.IDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.beans.PropertyChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

@Service("DeviceService")
public class DeviceService implements ICRUDService<IdentificationDeviceEntity> {

    private final IDeviceRepository deviceRepository;
    private final IEventManager eventManager;

    private final IDateTime dateTime;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public DeviceService(@Autowired IDeviceRepository deviceRepository,
                         @Autowired @Qualifier("EventManager") IEventManager eventManager,
                         @Autowired @Qualifier("DateTime")IDateTime dateTime) {
        this.deviceRepository = deviceRepository;
        this.eventManager = eventManager;
        this.dateTime = dateTime;
        eventManager.addListener(Event.DEVICE_CONNECTED, this::handleConnectedDevice);
        eventManager.addListener(Event.DEVICE_STATUS_UPDATE, this::handleUpdatedDeviceStatus);
    }

    private void invokeConnectionError(ConnectedDeviceDto device, String message) {
        var errorDto = new ConnectedDeviceErrorDto(
                dateTime.getEpochSeconds(),
                device,
                message
        );
        eventManager.invoke(Event.DEVICE_CONNECTED_ERROR, errorDto);
    }

    private void handleUpdatedDeviceStatus(PropertyChangeEvent propertyChangeEvent) {
        var deviceStatus = (DeviceStatusDto) propertyChangeEvent.getNewValue();
        var device = deviceRepository.findByDeviceId(deviceStatus.getDeviceId());
        if (device != null) {
            switch (deviceStatus.getState().toUpperCase()) {
                case ("OFFLINE"):
                    logger.info(String.format("Device offline: %s", device.getDeviceId()));
                    eventManager.invoke(Event.DEVICE_OFFLINE, device);
                    // TODO: Maybe save event (Key insight?)
                    break;
                case ("ONLINE"):
                    logger.info(String.format("Device online: %s", device.getDeviceId()));
                    eventManager.invoke(Event.DEVICE_ONLINE, device);
                    // TODO: Maybe save event (Key insight?)
                    break;
                case ("READY"):
                    logger.info(String.format("Device ready: %s", device.getDeviceId()));
                    eventManager.invoke(Event.DEVICE_READY, device);
                    break;
                case ("ACTIVE"):
                    logger.info(String.format("Device active: %s", device.getDeviceId()));
                    eventManager.invoke(Event.DEVICE_ACTIVE, device);
                    // TODO: Maybe save event (Key insight?)
                    break;
                default:
                    logger.info(String.format("Device status error: %s unknown state.", device.getDeviceId()));
                    break;
            }
        }
        else {
            logger.warn("Unknown device announced status update. Should not be possible!");
        }
    }

    private void handleConnectedDevice(PropertyChangeEvent propertyChangeEvent) {
        var connectedDevice = (ConnectedDeviceDto) propertyChangeEvent.getNewValue();

        // Check if device is known.
        var device = deviceRepository.findByDeviceId(connectedDevice.getDeviceId());

        // If not known persist the new device.
        if (device == null) {
            logger.info("New device connected. Creating creating device: " + connectedDevice.getDeviceId());
            device = create(new IdentificationDeviceEntity(
                    connectedDevice.getCompanyId(),
                    connectedDevice.getDeviceId(),
                    connectedDevice.getDeviceType()));
        }

        // Checks if the connected device exists and belongs to the correct company before init communication.
        if (device == null) {
            logger.error("Unable to create device: " + connectedDevice.getDeviceId());
            invokeConnectionError(connectedDevice, "Unable to verify connected device.");
            return;
        }
        else if (!connectedDevice.getCompanyId().equals(device.getCompanyId())) {
            invokeConnectionError(connectedDevice, "Device id must be unique.");
            return;
        }
        eventManager.invoke(Event.INIT_DEVICE_COMM, device);
        eventManager.invoke(Event.DEVICE_ONLINE, device);
        // TODO: Maybe save event (Key insight?)
    }

    @Override
    public List<IdentificationDeviceEntity> readAll() {
        var devices = new ArrayList<IdentificationDeviceEntity>();
        deviceRepository.findAll().forEach(devices::add);
        return devices;
    }

    @Override
    public IdentificationDeviceEntity readById(Long id) {
        if (deviceRepository.findById(id).isPresent()) {
            return deviceRepository.findById(id).get();
        }
        return null;
    }

    @Override
    public IdentificationDeviceEntity create(IdentificationDeviceEntity entity) {
        return deviceRepository.save(entity);
    }

    @Override
    public IdentificationDeviceEntity update(Long id, IdentificationDeviceEntity entity) {return null;}

    @Override
    public void delete(Long id) {}
}
