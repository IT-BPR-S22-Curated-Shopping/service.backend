package bpr.service.backend.persistence.repository.locationRepository;

import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import bpr.service.backend.models.entities.LocationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

public interface ILocationRepository extends CrudRepository<LocationEntity, Long> {

    LocationEntity findByIdentificationDevicesIn(List<IdentificationDeviceEntity> identificationDevices);
    Iterable<LocationEntity> findAll();
}
