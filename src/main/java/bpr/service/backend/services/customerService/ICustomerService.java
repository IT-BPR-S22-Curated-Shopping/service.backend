package bpr.service.backend.services.customerService;

import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;

import java.util.List;

public interface ICustomerService {

    CustomerEntity getOrCreateCustomerFromUUID(String uuid);

    CustomerEntity addUuidToCustomer(Long customerId, String uuid);

    CustomerEntity addTagsToCustomer(Long customerId, List<TagEntity> tags);

    List<ProductEntity> getProfileProducts(Long customerId, int size);
}
