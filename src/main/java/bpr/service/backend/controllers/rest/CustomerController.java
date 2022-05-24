package bpr.service.backend.controllers.rest;

import bpr.service.backend.models.entities.CustomerEntity;
import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.services.customerService.ICustomerService;
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

    private final ICustomerService customerService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public CustomerController(@Autowired @Qualifier("CustomerService") ICustomerService customerService) {

        this.customerService = customerService;
    }


    @GetMapping(value = "/uuid/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerEntity getOrCreateCustomerFromUUID(@PathVariable("uuid") String uuid) {
        return customerService.getOrCreateCustomerFromUUID(uuid);
    }

    @PutMapping(value="/{customerId}/uuid/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerEntity addUuidToCustomer(@PathVariable("customerId") Long id, @PathVariable("uuid") String uuid) {
        return customerService.addUuidToCustomer(id, uuid);
    }

    @PutMapping(value = "/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerEntity addTagsToCustomer(@PathVariable("customerId") Long customerId, @RequestBody(required = false) List<TagEntity> tags) {
        return customerService.addTagsToCustomer(customerId, tags);
    }

    @GetMapping(value = "/profileproducts/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductEntity> getProfileProducts(@PathVariable("customerId") Long customerId, @RequestParam(defaultValue = "10") int size) {
        return customerService.getProfileProducts(customerId, size);
    }

}
