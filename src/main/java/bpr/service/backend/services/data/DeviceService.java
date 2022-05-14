package bpr.service.backend.services.data;

import bpr.service.backend.models.dto.ConnectedDeviceDto;
import bpr.service.backend.models.dto.ConnectedDeviceErrorDto;
import bpr.service.backend.models.dto.DeviceStatusDto;
import bpr.service.backend.models.entities.TrackerEntity;
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
public class DeviceService implements ICRUDService<TrackerEntity> {

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
        eventManager.addListener(Event.DEVICE_CONNECTED, this::HandleConnectedDevice);
        eventManager.addListener(Event.DEVICE_STATUS_UPDATE, this::HandleUpdatedDeviceStatus);
    }

    private void invokeConnectionError(ConnectedDeviceDto device, String message) {
        var errorDto = new ConnectedDeviceErrorDto(
                dateTime.getEpochSeconds(),
                device,
                String.format("Device connection error: %s", message)
        );
        eventManager.invoke(Event.DEVICE_CONNECTED_ERROR, errorDto);
    }

    private void HandleUpdatedDeviceStatus(PropertyChangeEvent propertyChangeEvent) {
        var deviceStatus = (DeviceStatusDto) propertyChangeEvent.getNewValue();
        var device = deviceRepository.findByDeviceId(deviceStatus.getDeviceId());
        if (device != null) {
            if ((deviceStatus.isOnline())) {
                logger.info(String.format("Device online: %s", device.getDeviceId()));
                eventManager.invoke(Event.DEVICE_ONLINE, device);
            }
            else {
                logger.info(String.format("Device offline: %s", device.getDeviceId()));
                eventManager.invoke(Event.DEVICE_OFFLINE, device);
            }
        }
        else {
            logger.warn("Unknown device announced status update. Should not be possible!");
        }
    }

    private void HandleConnectedDevice(PropertyChangeEvent propertyChangeEvent) {
        var connectedDevice = (ConnectedDeviceDto) propertyChangeEvent.getNewValue();

        // Check if device is known.
        var device = deviceRepository.findByDeviceId(connectedDevice.getDeviceId());

        // If not known persist the new device.
        if (device == null) {
            logger.info("New device connected. Creating creating device: " + connectedDevice.getDeviceId());
            device = Create(new TrackerEntity(
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
        eventManager.invoke(Event.DEVICE_INIT_COMM, device);
    }

    @Override
    public List<TrackerEntity> ReadAll() {
        var devices = new ArrayList<TrackerEntity>();
        deviceRepository.findAll().forEach(devices::add);
        return devices;
    }

    @Override
    public TrackerEntity ReadById(Long id) {
        if (deviceRepository.findById(id).isPresent()) {
            return deviceRepository.findById(id).get();
        }
        return null;
    }

    @Override
    public TrackerEntity Create(TrackerEntity entity) {
        return deviceRepository.save(entity);
    }

    @Override
    public TrackerEntity Update(Long id, TrackerEntity entity) {return null;}

    @Override
    public void Delete(Long id) {}
}
