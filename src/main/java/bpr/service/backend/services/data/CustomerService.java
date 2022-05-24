package bpr.service.backend.services.data;

import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.UuidEntity;
import bpr.service.backend.persistence.repository.customerRepository.ICustomerRepository;
import bpr.service.backend.persistence.repository.customerRepository.IUuidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("CustomerService")
public class CustomerService implements ICRUDService<CustomerEntity>{


    private final ICustomerRepository customerRepository;
    private final IUuidRepository uuidRepository;

    public CustomerService(@Autowired ICustomerRepository customerRepository, @Autowired IUuidRepository uuidRepository) {
        this.customerRepository = customerRepository;
        this.uuidRepository = uuidRepository;
    }

    public CustomerEntity getCustomerByUUID(String uuid) {
        UuidEntity uuidEntity = uuidRepository.findByUuid(uuid);
        if (uuidEntity != null) {
            return customerRepository.findByUuids(uuidEntity);
        }
        return null;
    }


    @Override
    public List<CustomerEntity> readAll() {
        return null;
    }

    @Override
    public CustomerEntity readById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public CustomerEntity create(CustomerEntity entity) {
        return customerRepository.save(entity);
    }

    @Override
    public CustomerEntity update(Long id, CustomerEntity entity) {
        return customerRepository.save(entity);
    }

    @Override
    public void delete(Long id) {

    }
}
