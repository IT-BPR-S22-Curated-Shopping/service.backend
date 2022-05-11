package bpr.service.backend.services.data;

import bpr.service.backend.data.entities.TrackerEntity;
import bpr.service.backend.persistence.repository.deviceRepository.IDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("DeviceService")
public class DeviceService implements ICRUDService<TrackerEntity> {

    private final IDeviceRepository deviceRepository;

    public DeviceService(@Autowired IDeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
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
    public TrackerEntity Create(TrackerEntity entity) {return null;}

    @Override
    public TrackerEntity Update(Long id, TrackerEntity entity) {return null;}

    @Override
    public void Delete(Long id) {}
}
