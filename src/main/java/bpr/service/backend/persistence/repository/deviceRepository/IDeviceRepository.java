package bpr.service.backend.persistence.repository.deviceRepository;

import bpr.service.backend.data.entities.TrackerEntity;
import org.springframework.data.repository.CrudRepository;

public interface IDeviceRepository extends CrudRepository<TrackerEntity, Long> {

}