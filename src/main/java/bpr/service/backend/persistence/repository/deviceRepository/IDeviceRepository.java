package bpr.service.backend.persistence.repository.deviceRepository;

import bpr.service.backend.models.entities.IdentificationDeviceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDeviceRepository extends CrudRepository<IdentificationDeviceEntity, Long> {
    IdentificationDeviceEntity findByDeviceId(String deviceId);

    @Query(value = "SELECT * FROM id_devices WHERE id_devices.id NOT IN (SELECT identification_devices_id from locations_identification_devices)" , nativeQuery = true)
    List<IdentificationDeviceEntity> findAllAvailable();

}
