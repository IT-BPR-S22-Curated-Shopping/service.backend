package bpr.service.backend.services.locationService;

import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.models.entities.LocationEntity;

import java.util.List;

public interface ILocationService {

    LocationEntity create(LocationEntity entity);
    LocationEntity createLocation(String name, Long productId, List<String> deviceIds);
    LocationEntity updateWithDeviceList(Long id, List<IdentificationDeviceEntity> deviceList);
    LocationEntity update(Long id, LocationEntity entity);
    List<LocationEntity> readAll();
    LocationEntity readById(Long id);
    void delete(Long id);
}
