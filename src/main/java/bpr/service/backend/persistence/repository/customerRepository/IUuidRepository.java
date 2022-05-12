package bpr.service.backend.persistence.repository.customerRepository;

import bpr.service.backend.models.entities.UuidEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUuidRepository extends CrudRepository<UuidEntity, Long> {
    UuidEntity findByUuid(String uuid);
}
