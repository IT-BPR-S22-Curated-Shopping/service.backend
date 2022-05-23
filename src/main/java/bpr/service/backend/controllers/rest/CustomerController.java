package bpr.service.backend.controllers.rest;

import bpr.service.backend.models.entities.*;
import bpr.service.backend.models.recommender.ProductRecommendation;
import bpr.service.backend.persistence.repository.customerRepository.IUuidRepository;
import bpr.service.backend.services.data.CustomerService;
import bpr.service.backend.services.data.ICRUDService;
import bpr.service.backend.services.recommender.IRecommender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final ICRUDService<CustomerEntity> customerService;
    private final IUuidRepository uuidRepository;
    private final IRecommender recommender;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public CustomerController(@Autowired @Qualifier("CustomerService") ICRUDService<CustomerEntity> customerService,
                              @Autowired IUuidRepository uuidRepository,
                              @Autowired @Qualifier("RecommenderJaccardIndexMergedTags") IRecommender recommender) {
        this.customerService = customerService;
        this.uuidRepository = uuidRepository;
        this.recommender = recommender;
    }


    @GetMapping(value = "/uuid/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerEntity getOrCreateCustomerFromUUID(@PathVariable("uuid") String uuid) {
        var customer = ((CustomerService) customerService).getCustomerByUUID(uuid);
        if (customer == null) {
            UuidEntity uuidEntity = uuidRepository.findByUuid(uuid);
            if (uuidEntity == null) {
                uuidEntity = uuidRepository.save(new UuidEntity(uuid));
            }
            customer = customerService.create(new CustomerEntity(List.of(uuidEntity), null));
        }

        return customer;
    }

    @PutMapping(value = "/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerEntity addTagsToCustomer(@PathVariable("customerId") Long customerId, @RequestBody List<TagEntity> tags) {
        var customer = customerService.readById(customerId);
        if (customer != null) {
            var tagEntities = customer.getTags();
            tagEntities.addAll(tags);
            customer.setTags(tagEntities);
        }
        return customer;
    }

    @GetMapping(value = "/profileproducts/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductEntity> getProfileProducts(@PathVariable("customerId") Long customerId, @RequestParam(defaultValue = "10") int size) {
        var customer = customerService.readById(customerId);
        if (customer != null) {
            var profileProducts = recommender.getProfileProducts(customer, size);
            if (profileProducts != null) {
                return profileProducts;
            } else {
                return null;
            }
        }
        return null;
    }

}
