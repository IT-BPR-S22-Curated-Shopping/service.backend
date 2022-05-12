package bpr.service.backend.services.data;

import bpr.service.backend.models.dto.ConnectedDeviceDto;
import bpr.service.backend.models.dto.DeviceStatusDto;
import bpr.service.backend.models.entities.TrackerEntity;
import bpr.service.backend.managers.events.Event;
import bpr.service.backend.managers.events.IEventManager;
import bpr.service.backend.persistence.repository.deviceRepository.IDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

@Service("DeviceService")
public class DeviceService implements ICRUDService<TrackerEntity> {

    private final IDeviceRepository deviceRepository;
    private final IEventManager eventManager;

    public DeviceService(@Autowired IDeviceRepository deviceRepository,
                         @Autowired @Qualifier("EventManager") IEventManager eventManager) {
        this.deviceRepository = deviceRepository;
        this.eventManager = eventManager;
        eventManager.addListener(Event.DEVICE_CONNECTED, this::HandleConnectedDevice);
        eventManager.addListener(Event.DEVICE_STATUS_UPDATE, this::HandleUpdatedDeviceStatus);
    }

    private void HandleUpdatedDeviceStatus(PropertyChangeEvent propertyChangeEvent) {
        var deviceStatus = (DeviceStatusDto) propertyChangeEvent.getNewValue();
        var device = deviceRepository.findByDeviceId(deviceStatus.getDeviceId());
        if (device != null) {
            if ((deviceStatus.getStatus().equalsIgnoreCase("ONLINE"))) {
                eventManager.invoke(Event.DEVICE_ONLINE, device);
            }
            else {
                eventManager.invoke(Event.DEVICE_OFFLINE, device);
            }
        }
    }

    private void HandleConnectedDevice(PropertyChangeEvent propertyChangeEvent) {
        var connectedDevice = (ConnectedDeviceDto) propertyChangeEvent.getNewValue();
        var device = deviceRepository.findByDeviceId(connectedDevice.getDeviceId());
        if (device == null) {
            device = Create(new TrackerEntity(
                    connectedDevice.getCompanyId(),
                    connectedDevice.getDeviceId(),
                    connectedDevice.getDeviceType()));
        }
        if (device != null)
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
