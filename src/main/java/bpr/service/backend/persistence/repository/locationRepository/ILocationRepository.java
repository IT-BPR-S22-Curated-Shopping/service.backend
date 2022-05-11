package bpr.service.backend.persistence.repository.locationRepository;

import bpr.service.backend.data.entities.LocationEntity;
import org.springframework.data.repository.CrudRepository;

public interface ILocationRepository extends CrudRepository<LocationEntity, Long> {

    Iterable<LocationEntity> findAll();
}
