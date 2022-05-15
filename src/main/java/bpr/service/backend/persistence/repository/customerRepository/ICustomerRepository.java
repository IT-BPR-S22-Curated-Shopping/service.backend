package bpr.service.backend.persistence.repository.customerRepository;

import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.UuidEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICustomerRepository extends CrudRepository<CustomerEntity, Long> {
    CustomerEntity findByUuids(UuidEntity uuidEntity);

}
