package bpr.service.backend.controllers.rest;

import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.services.customerService.ICustomerService;
import bpr.service.backend.util.ISerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final ICustomerService customerService;
    private final ISerializer serializer;


    public CustomerController(@Autowired @Qualifier("CustomerService") ICustomerService customerService,
                              @Autowired @Qualifier("JsonSerializer") ISerializer serializer) {
        this.customerService = customerService;
        this.serializer = serializer;
    }


    @GetMapping(value = "/uuid/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getOrCreateCustomerFromUUID(@PathVariable("uuid") String uuid) {
        ResponseEntity<String> response;
        if (!uuid.isEmpty()) {
            var customer = customerService.getOrCreateCustomerFromUUID(uuid);
            if (customer != null) {
                response = new ResponseEntity<>(serializer.toJson(customer), HttpStatus.OK);
            } else {
                response = new ResponseEntity<>("Cannot find a customer with the given UUID", HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>("UUID cannot be empty", HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @PutMapping(value = "/{customerId}/uuid/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> addUuidToCustomer(@PathVariable("customerId") Long id, @PathVariable("uuid") String uuid) {
        ResponseEntity<String> response;
        if (id != 0 && !uuid.isEmpty()) {
            var customer = customerService.addUuidToCustomer(id, uuid);
            if (customer != null) {
                response = new ResponseEntity<>(serializer.toJson(customer), HttpStatus.OK);
            } else {
                response = new ResponseEntity<>("Could not add uuid to customer", HttpStatus.BAD_GATEWAY);
            }
        } else {
            response = new ResponseEntity<>("Input must contain valid customer Id and uuid", HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @PutMapping(value = "/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> addTagsToCustomer(@PathVariable("customerId") Long customerId, @RequestBody(required = false) List<TagEntity> tags) {
        ResponseEntity<String> response;

        if (customerId != 0 || tags != null && tags.size() > 0) {
            var customer = customerService.addTagsToCustomer(customerId, tags);
            if (customer != null) {
                response = new ResponseEntity<>(serializer.toJson(customer), HttpStatus.OK);
            } else {
                response = new ResponseEntity<>("could not add tags to customer.", HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>("Input must contain valid customerId and tag entities.", HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @GetMapping(value = "/profileproducts/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getProfileProducts(@PathVariable("customerId") Long customerId, @RequestParam(defaultValue = "10") int size) {
        ResponseEntity<String> response;

        if (customerId != 0 || size > 0) {
            var customer = customerService.getProfileProducts(customerId, size);
            if (customer != null) {
                response = new ResponseEntity<>(serializer.toJson(customer), HttpStatus.OK);
            } else {
                response = new ResponseEntity<>("could not get profile products.", HttpStatus.BAD_GATEWAY);
            }
        } else {
            response = new ResponseEntity<>("Input must contain valid customerId and over size 0", HttpStatus.BAD_REQUEST);
        }
        return response;
    }

}
