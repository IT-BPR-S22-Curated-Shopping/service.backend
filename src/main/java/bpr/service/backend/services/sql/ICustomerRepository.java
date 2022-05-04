package bpr.service.backend.services.sql;

import bpr.service.backend.models.sql.CustomerEntity;

import java.util.Optional;

public interface ICustomerRepository {

    CustomerEntity addCustomer(CustomerEntity customer);
    Iterable<CustomerEntity> findAllCustomers();
    CustomerEntity findCustomerByUUID(String uuid);
    Optional<CustomerEntity> findCustomerById(Long id);
}
