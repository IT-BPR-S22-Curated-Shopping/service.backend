package bpr.service.backend.persistence.repository.customerRepository;

import bpr.service.backend.models.sql.CustomerEntity;
import org.springframework.data.repository.Repository;

public interface ICustomerRepository extends Repository<CustomerEntity, Long> {

    CustomerEntity findByUuid(String uuid);
}
