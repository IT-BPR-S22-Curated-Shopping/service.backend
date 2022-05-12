package bpr.service.backend.persistence.repository.locationRepository;

import bpr.service.backend.models.entities.LocationEntity;
import org.springframework.data.repository.CrudRepository;

public interface ILocationRepository extends CrudRepository<LocationEntity, Long> {

    Iterable<LocationEntity> findAll();
}
