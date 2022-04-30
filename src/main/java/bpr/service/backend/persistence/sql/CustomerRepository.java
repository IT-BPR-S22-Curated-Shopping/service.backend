package bpr.service.backend.persistence.sql;

import bpr.service.backend.models.sql.CustomerEntity;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<CustomerEntity, Long> {

    CustomerEntity findByUuid(String uuid);
}
