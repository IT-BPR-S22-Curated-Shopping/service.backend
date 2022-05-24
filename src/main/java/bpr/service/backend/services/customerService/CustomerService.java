package bpr.service.backend.services.customerService;

import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.models.entities.UuidEntity;
import bpr.service.backend.persistence.repository.customerRepository.ICustomerRepository;
import bpr.service.backend.persistence.repository.customerRepository.IUuidRepository;
import bpr.service.backend.services.recommender.IRecommender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service("CustomerService")
public class CustomerService implements ICustomerService {


    private final ICustomerRepository customerRepository;
    private final IUuidRepository uuidRepository;
    private final IRecommender recommender;

    public CustomerService(@Autowired ICustomerRepository customerRepository, @Autowired IUuidRepository uuidRepository,
                           @Autowired @Qualifier("RecommenderJaccardIndexMergedTags") IRecommender recommender) {
        this.customerRepository = customerRepository;
        this.uuidRepository = uuidRepository;
        this.recommender = recommender;
    }


    @Override
    public CustomerEntity getOrCreateCustomerFromUUID(String uuid) {

        var uuidEntity = uuidRepository.findByUuid(uuid);
        var customer = customerRepository.findByUuids(uuidEntity);
        if (customer == null) {
            if (uuidEntity == null) {
                uuidEntity = uuidRepository.save(new UuidEntity(uuid));
            }
            customer = customerRepository.save(new CustomerEntity(List.of(uuidEntity), null));
        }
        return customer;
    }


    @Override
    public CustomerEntity addUuidToCustomer(Long customerId, String uuid) {
        var customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            List<UuidEntity> uuids = customer.getUuids();
            uuids.add(new UuidEntity(uuid));
            customer.setUuids(uuids);
            customer = customerRepository.save(customer);
        }
        return customer;
    }

    @Override
    public CustomerEntity addTagsToCustomer(Long customerId, List<TagEntity> tags) {
        var customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null && tags != null && tags.size() > 0) {
            Set<TagEntity> tagEntities = new LinkedHashSet<>(customer.getTags());
            tagEntities.addAll(tags);
            customer.setTags(new ArrayList<>(tagEntities));
            customer = customerRepository.save(customer);
        }
        return customer;
    }

    @Override
    public List<ProductEntity> getProfileProducts(Long customerId, int size) {
        var customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            return recommender.getProfileProducts(customer, size);
        }
        return null;
    }

}
