package bpr.service.backend.services.data;

import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.models.dto.ConnectedDeviceDto;
import bpr.service.backend.models.dto.ConnectedDeviceErrorDto;
import bpr.service.backend.models.dto.DeviceStatusDto;
import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.persistence.repository.deviceRepository.IDeviceRepository;
import bpr.service.backend.util.IDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.beans.PropertyChangeEvent;
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
        setAllDeviceStateOffline();
        eventManager.addListener(Event.DEVICE_CONNECTED, this::handleConnectedDevice);
        eventManager.addListener(Event.DEVICE_STATUS_UPDATE, this::handleUpdatedDeviceStatus);
    }

    // Ensures consistent data on initialization.
    private void setAllDeviceStateOffline() {
        var devices = readAll();
        for (var device : devices) {
            device.setTimestampOffline(dateTime.getEpochSeconds());
        }
        updateAll(devices);
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
        var deviceEntity = readByDeviceId(deviceStatus.getDeviceId());
        if (deviceEntity != null) {
            switch (deviceStatus.getState().toUpperCase()) {
                case ("OFFLINE"):
                    logger.info(String.format("Device offline: %s", deviceEntity.getDeviceId()));
                    deviceEntity.setTimestampOffline(deviceStatus.getTimestamp());
                    eventManager.invoke(Event.DEVICE_OFFLINE, deviceEntity);
                    break;
                case ("ONLINE"):
                    logger.info(String.format("Device online: %s", deviceEntity.getDeviceId()));
                    deviceEntity.setTimeStampOnline(deviceStatus.getTimestamp());
                    eventManager.invoke(Event.DEVICE_ONLINE, deviceEntity);
                    break;
                case ("READY"):
                    logger.info(String.format("Device ready: %s", deviceEntity.getDeviceId()));
                    deviceEntity.setTimestampReady(deviceStatus.getTimestamp());
                    eventManager.invoke(Event.DEVICE_READY, deviceEntity);
                    break;
                case ("ACTIVE"):
                    logger.info(String.format("Device active: %s", deviceEntity.getDeviceId()));
                    deviceEntity.setTimestampActive(deviceStatus.getTimestamp());
                    eventManager.invoke(Event.DEVICE_ACTIVE, deviceEntity);
                    break;
                default:
                    logger.info(String.format("Device status error: %s unknown state.", deviceEntity.getDeviceId()));
                    return;
            }
            update(deviceEntity);
        }
        else {
            logger.warn("Unknown device announced status update. Should not be possible!");
        }
    }

    private void handleConnectedDevice(PropertyChangeEvent propertyChangeEvent) {
        var connectedDevice = (ConnectedDeviceDto) propertyChangeEvent.getNewValue();

        // Check if device is known.
        var deviceEntity = readByDeviceId(connectedDevice.getDeviceId());

        // If not known persist the new device.
        if (deviceEntity == null) {
            logger.info("New device connected. Creating creating device: " + connectedDevice.getDeviceId());

            deviceEntity = create(new IdentificationDeviceEntity(
                    connectedDevice.getCompanyId(),
                    connectedDevice.getDeviceId(),
                    connectedDevice.getDeviceType(),
                    dateTime.getEpochSeconds()));
        }

        // Checks if the connected device exists and belongs to the correct company before init communication.
        if (deviceEntity == null) {
            logger.error("Unable to create device: " + connectedDevice.getDeviceId());
            invokeConnectionError(connectedDevice, "Unable to verify connected device.");
            return;
        }
        else if (!connectedDevice.getCompanyId().equals(deviceEntity.getCompanyId())) {
            invokeConnectionError(connectedDevice, "Device id must be unique.");
            return;
        }
        deviceEntity.setTimeStampOnline(connectedDevice.getTimestamp());
        update(deviceEntity);
        eventManager.invoke(Event.INIT_DEVICE_COMM, deviceEntity);
        eventManager.invoke(Event.DEVICE_ONLINE, deviceEntity);
    }

    @Override
    public List<IdentificationDeviceEntity> readAll() {
        var devices = new ArrayList<IdentificationDeviceEntity>();
        deviceRepository.findAll().forEach(devices::add);
        return devices;
    }

    public List<IdentificationDeviceEntity> readAllAvailable() {
        var devices = new ArrayList<IdentificationDeviceEntity>(deviceRepository.findAllAvailable());
        return devices;
    }

    @Override
    public IdentificationDeviceEntity readById(Long id) {
        if (deviceRepository.findById(id).isPresent()) {
            return deviceRepository.findById(id).get();
        }
        return null;
    }
    private IdentificationDeviceEntity readByDeviceId(String deviceId) {
        return  deviceRepository.findByDeviceId(deviceId);
    }

    @Override
    public IdentificationDeviceEntity create(IdentificationDeviceEntity entity) {
        return deviceRepository.save(entity);
    }

    @Override
    public IdentificationDeviceEntity update(Long id, IdentificationDeviceEntity entity) {return null;}

    private IdentificationDeviceEntity update(IdentificationDeviceEntity entity) {
        return deviceRepository.save(entity);
    }

    private Iterable<IdentificationDeviceEntity> updateAll(List<IdentificationDeviceEntity> entities) {
        return deviceRepository.saveAll(entities);
    }

    @Override
    public void delete(Long id) {}
}
