package bpr.service.backend.persistence.repository.customerRepository;

import bpr.service.backend.models.entities.CustomerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICustomerRepository extends CrudRepository<CustomerEntity, Long> {

    CustomerEntity findByUuid(String uuid);
}
