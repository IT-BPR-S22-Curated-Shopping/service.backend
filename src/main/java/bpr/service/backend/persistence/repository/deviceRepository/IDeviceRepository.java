package bpr.service.backend.persistence.repository.deviceRepository;

import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDeviceRepository extends CrudRepository<IdentificationDeviceEntity, Long> {
    IdentificationDeviceEntity findByDeviceId(String deviceId);


}
